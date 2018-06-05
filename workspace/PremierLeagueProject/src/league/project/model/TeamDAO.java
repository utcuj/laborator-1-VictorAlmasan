package league.project.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import league.project.exception.DuplicateEntryException;
import league.project.exception.TeamDAOException;
import league.project.factory.ModelFactory;
import league.project.util.ApplicationData;

public class TeamDAO {

	public static ObservableList<Team> getTeamList(String teamNameField, String headCoachField, String groundField)
			throws TeamDAOException {
		ObservableList<Team> teamsData = FXCollections.observableArrayList();

		boolean firstCriteria = (StringUtils.isBlank(teamNameField) ? true : false);
		boolean secondCriteria = (StringUtils.isBlank(headCoachField) ? true : false);
		boolean thirdCriteria = (StringUtils.isBlank(groundField) ? true : false);

		StringBuilder query = new StringBuilder(
				"SELECT t.id_team, t.budget, t.team_name, t.team_short_name, c.coach_name, c.coach_code, t.ground, t.founded, t.team_status_code FROM team t INNER JOIN coach c ON t.coach_code=c.coach_code WHERE 1=1 ");
		if (!firstCriteria) {
			query.append(" AND UPPER(t.team_name) LIKE UPPER(?) ");
		}
		if (!secondCriteria) {
			query.append(" AND UPPER(c.coach_name) LIKE UPPER(?) ");
		}
		if (!thirdCriteria) {
			query.append(" AND UPPER(t.ground) LIKE UPPER(?) ");
		}
		query.append("ORDER BY team_status_code, t.team_name");

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query.toString())) {
			int i = 1;
			if (!firstCriteria) {
				stmt.setString(i, "%" + teamNameField.trim() + "%");

				i++;
			}
			if (!secondCriteria) {
				stmt.setString(i, "%" + headCoachField.trim() + "%");
				i++;
			}
			if (!thirdCriteria) {
				stmt.setString(i, "%" + groundField.trim() + "%");
			}

			try (ResultSet rs = stmt.executeQuery()) {

				while (rs.next()) {

					int teamId = rs.getInt("id_team");
					int budget = rs.getInt("budget");
					String teamName = rs.getString("team_name");
					String shortNameTeam = rs.getString("team_short_name");
					String headCoach = rs.getString("c.coach_name");
					String headCoachCode = rs.getString("c.coach_code");
					String ground = rs.getString("ground");
					Date dateTeam = rs.getDate("founded");
					String status = rs.getString("team_status_code");
					
					ModelFactory modelFactory = new ModelFactory();
					
					Coach coach = (Coach) modelFactory.getModel("coach");
					coach.setCoachName(headCoach);
					coach.setCoachCode(headCoachCode);
					
					
					Team team = (Team) modelFactory.getModel("team");
					team.setTeamId(teamId);
					team.setBudget(budget);
					team.setTeamName(teamName);
					team.setShortName(shortNameTeam);
					team.setHeadCoach(coach);
					team.setGround(ground);
					team.setFounded(dateTeam.toLocalDate());
					team.setStatus(status);
					team.setStatusImg(new ImageView(new Image(ApplicationData.TEAM_ACTIVE.equalsIgnoreCase(status)
									? "/images/greenC.png" : "/images/redC.png")));
					
					teamsData.add(team);
				}
			}
		} catch (Exception e) {
			throw new TeamDAOException(e);
		}

		return teamsData;
	}

	public static int insertTeam(Team team) throws DuplicateEntryException,TeamDAOException {

		String query = "INSERT INTO team (budget, team_name, team_short_name, coach_code, ground, founded, team_status_code)"
				+ " VALUES (?, ucfirst(?),UPPER(?), ?, ?, ?, ?)";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setInt(1, team.getBudget());
			stmt.setString(2, team.getTeamName());
			stmt.setString(3, team.getShortName());
			stmt.setString(4, team.getHeadCoach().getCoachCode());
			stmt.setString(5, team.getGround());
			stmt.setDate(6, java.sql.Date.valueOf(team.getFounded()));
			stmt.setString(7, ApplicationData.TEAM_ACTIVE);

			stmt.executeUpdate();
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new DuplicateEntryException(e);
		} catch (Exception e) {
			throw new TeamDAOException(e);
		}

		return 0;
	}

	public static void editTeam(Team team) throws TeamDAOException {

		String query = "UPDATE team SET budget = ?, team_name = ?, team_short_name = ?, coach_code = ?, ground = ?, founded = ?, team_status_code = ? WHERE id_team = ?";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement st = co.prepareStatement(query)) {

			st.setInt(1, team.getBudget());
			st.setString(2, team.getTeamName());
			st.setString(3, team.getShortName());
			st.setString(4, team.getHeadCoach().getCoachCode());
			st.setString(5, team.getGround());
			st.setDate(6, java.sql.Date.valueOf(team.getFounded()));
			st.setString(7, team.getStatus());
			st.setInt(8, team.getTeamId());
			st.executeUpdate();

		} catch (Exception e) {
			throw new TeamDAOException(e);
		}

	}

	public static boolean deleteTeam(int teamId) throws TeamDAOException {

		String query = "UPDATE team SET team_status_code = ? WHERE id_team = ?";
		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement st = co.prepareStatement(query)) {
			st.setString(1, ApplicationData.TEAM_INACTIVE);
			st.setInt(2, teamId);
			if (st.executeUpdate() > 0) {
				return true;
			}
		} catch (Exception e) {
			throw new TeamDAOException(e);
		}

		return false;
	}

	public static ObservableList<Coach> getCoachesList() throws TeamDAOException {

		ObservableList<Coach> coachesData = FXCollections.observableArrayList();
		String query = "SELECT coach_code, coach_name FROM coach WHERE coach_code NOT IN (SELECT DISTINCT coach_code FROM team WHERE team_status_code=?) ORDER BY coach_name";
		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement st = co.prepareStatement(query)) {
			st.setString(1, ApplicationData.TEAM_ACTIVE);
			ModelFactory modelFactory = new ModelFactory();
			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					Coach coach = (Coach) modelFactory.getModel("coach");
					coach.setCoachCode(rs.getString("coach_code"));
					coach.setCoachName(rs.getString("coach_name"));
					coachesData.add(coach);
				}
			}
		} catch (Exception e) {
			throw new TeamDAOException(e);
		}
		return coachesData;
	}

}
