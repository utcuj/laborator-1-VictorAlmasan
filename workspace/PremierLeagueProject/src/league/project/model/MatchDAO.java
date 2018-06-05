package league.project.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.apache.commons.lang.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import league.project.exception.MatchDAOException;
import league.project.factory.ModelFactory;
import league.project.util.ApplicationData;

public class MatchDAO {

	public static ObservableList<Match> getMatchesList() throws MatchDAOException {
		ObservableList<Match> matchesData = FXCollections.observableArrayList();

		String query = "SELECT m.id_match, m.id_home_team, m.id_away_team, m.match_date, m.match_status_code, t1.team_name, t1.ground, t2.team_name, mr.code, mr.name, mr.start_date, mr.end_date, m.nr_goals_away_team, m.nr_goals_home_team FROM football_match m INNER JOIN team t1 ON t1.id_team = m.id_home_team INNER JOIN team t2 ON t2.id_team = m.id_away_team LEFT JOIN match_round mr ON mr.code=m.round_code ORDER BY m.match_status_code, t1.team_name, t2.team_name";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query)) {

			try (ResultSet rs = stmt.executeQuery()) {

				while (rs.next()) {
					Date matchDate = rs.getDate("m.match_date");
					String matchStatusCode = rs.getString("m.match_status_code");
					
					ModelFactory modelFactory = new ModelFactory();

					Team homeTeam = (Team) modelFactory.getModel("team");
					homeTeam.setTeamId(rs.getInt("m.id_home_team"));
					homeTeam.setTeamName(rs.getString("t1.team_name"));
					homeTeam.setGround(rs.getString("t1.ground"));

					Team awayTeam =  (Team) modelFactory.getModel("team");
					awayTeam.setTeamId(rs.getInt("m.id_away_team"));
					awayTeam.setTeamName(rs.getString("t2.team_name"));

					MatchRound round =  (MatchRound) modelFactory.getModel("matchround");
					round.setCode(rs.getInt("mr.code"));
					round.setName(rs.getString("mr.name"));
					Date startDate = rs.getDate("mr.start_date");
					round.setStartDate(startDate == null ? null : startDate.toLocalDate());
					Date endDate = rs.getDate("mr.end_date");
					round.setEndDate(endDate == null ? null : endDate.toLocalDate());

					Match match = (Match) modelFactory.getModel("match");
					match.setMatchId(rs.getInt("m.id_match"));
					match.setHomeTeam(homeTeam);
					match.setAwayTeam(awayTeam);
					match.setRound(round);
					match.setNoGoalsAwayTeam(rs.getInt("m.nr_goals_away_team"));
					match.setNoGoalsHomeTeam(rs.getInt("m.nr_goals_home_team"));
					match.setMatchDate(matchDate == null ? null : matchDate.toLocalDate());
					match.setMatchStatusCode(matchStatusCode);
					match.setStatusImg(new ImageView(new Image(
							ApplicationData.MATCH_FINISHED.equalsIgnoreCase(matchStatusCode) ? "/images/greenC.png"
									: ApplicationData.MATCH_BANKRUPTCY.equalsIgnoreCase(matchStatusCode)
											? "/images/redC.png"
											: ApplicationData.MATCH_PENDING.equalsIgnoreCase(matchStatusCode)
													? "/images/blueC.png" : "/images/orangeC.png")));

					matchesData.add(match);
				}
			}
		} catch (Exception e) {
			throw new MatchDAOException(e);
		}

		return matchesData;
	}

	public static ObservableList<Contract> getPlayersList(int idHomeTeam, int idAwayTeam) throws MatchDAOException {
		ObservableList<Contract> contractsData = FXCollections.observableArrayList();

		String query = "SELECT c.id_player, c.id_team, (SELECT team_name FROM team WHERE id_team = c.id_team) as team_name, (SELECT first_name FROM player WHERE id_player=c.id_player) as first_name, (SELECT last_name FROM player WHERE id_player=c.id_player) as last_name FROM contract c WHERE (c.id_team = ? OR c.id_team = ?) AND c.status_code=? ORDER BY c.id_team ";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query)) {
			stmt.setInt(1, idHomeTeam);
			stmt.setInt(2, idAwayTeam);
			stmt.setString(3, ApplicationData.CONTRACT_ACTIVE);
			
			ModelFactory modelFactory = new ModelFactory();
			
			try (ResultSet rs = stmt.executeQuery()) {

				while (rs.next()) {

					Player player = (Player) modelFactory.getModel("player");
					player.setPlayerId(rs.getInt("c.id_player"));
					player.setFirstName(rs.getString("first_name"));
					player.setLastName(rs.getString("last_name"));

					Team team = (Team) modelFactory.getModel("team");
					team.setTeamId(rs.getInt("c.id_team"));
					team.setTeamName(rs.getString("team_name"));

					Contract contract = (Contract) modelFactory.getModel("contract");
					contract.setPlayer(player);
					contract.setTeam(team);
					contractsData.add(contract);
				}
			}
		} catch (Exception e) {
			throw new MatchDAOException(e);
		}

		return contractsData;
	}

	public static ObservableList<Match> getMatchListCriteria(String homeTeam, String awayTeam)
			throws MatchDAOException {
		ObservableList<Match> matchesData = FXCollections.observableArrayList();

		boolean firstCriteria = (StringUtils.isBlank(homeTeam) ? true : false);
		boolean secondCriteria = (StringUtils.isBlank(awayTeam) ? true : false);

		StringBuilder query = new StringBuilder(
				"SELECT m.id_match, m.id_home_team, m.id_away_team, m.match_date, m.match_status_code, t1.team_name, t1.ground, t2.team_name, mr.code, mr.name, mr.start_date, mr.end_date, m.nr_goals_home_team, m.nr_goals_away_team FROM football_match m INNER JOIN team t1 ON t1.id_team = m.id_home_team INNER JOIN team t2 ON t2.id_team = m.id_away_team LEFT JOIN match_round mr ON mr.code=m.round_code  WHERE  1=1 ");
		if (!firstCriteria) {
			query.append(" AND UPPER(t1.team_name) LIKE UPPER(?) ");
		}
		if (!secondCriteria) {
			query.append(" AND UPPER(t2.team_name) LIKE UPPER(?) ");
		}
		query.append("ORDER BY m.match_status_code, t1.team_name, t2.team_name");

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query.toString())) {

			int i = 1;
			if (!firstCriteria) {
				stmt.setString(i, "%" + homeTeam.trim() + "%");

				i++;
			}
			if (!secondCriteria) {
				stmt.setString(i, "%" + awayTeam.trim() + "%");
			}

			ModelFactory modelFactory = new ModelFactory();

			
			try (ResultSet rs = stmt.executeQuery()) {

				while (rs.next()) {
					Date matchDate = rs.getDate("m.match_date");
					String matchStatusCode = rs.getString("m.match_status_code");

					Team hTeam = (Team) modelFactory.getModel("team");
					hTeam.setTeamId(rs.getInt("m.id_home_team"));
					hTeam.setTeamName(rs.getString("t1.team_name"));
					hTeam.setGround(rs.getString("t1.ground"));

					Team aTeam = (Team) modelFactory.getModel("team");
					aTeam.setTeamId(rs.getInt("m.id_away_team"));
					aTeam.setTeamName(rs.getString("t2.team_name"));

					MatchRound round = (MatchRound) modelFactory.getModel("matchround");
					round.setCode(rs.getInt("mr.code"));
					round.setName(rs.getString("mr.name"));
					Date startDate = rs.getDate("mr.start_date");
					round.setStartDate(startDate == null ? null : startDate.toLocalDate());
					Date endDate = rs.getDate("mr.end_date");
					round.setEndDate(endDate == null ? null : endDate.toLocalDate());

					Match match = (Match) modelFactory.getModel("match");
					match.setMatchId(rs.getInt("m.id_match"));
					match.setHomeTeam(hTeam);
					match.setAwayTeam(aTeam);
					match.setRound(round);
					match.setNoGoalsAwayTeam(rs.getInt("m.nr_goals_away_team"));
					match.setNoGoalsHomeTeam(rs.getInt("m.nr_goals_home_team"));
					match.setMatchDate(matchDate == null ? null : matchDate.toLocalDate());
					match.setMatchStatusCode(matchStatusCode);
					match.setStatusImg(new ImageView(new Image(
							ApplicationData.MATCH_FINISHED.equalsIgnoreCase(matchStatusCode) ? "/images/greenC.png"
									: ApplicationData.MATCH_BANKRUPTCY.equalsIgnoreCase(matchStatusCode)
											? "/images/redC.png"
											: ApplicationData.MATCH_PENDING.equalsIgnoreCase(matchStatusCode)
													? "/images/blueC.png" : "/images/orangeC.png")));

					matchesData.add(match);
				}
			}
		} catch (Exception e) {
			throw new MatchDAOException(e);
		}
		return matchesData;
	}

	public static ObservableList<MatchRound> getMatchRoundList(int homeTeamId, int awayTeamId)
			throws MatchDAOException {
		ObservableList<MatchRound> matchRoundData = FXCollections.observableArrayList();

		// String query = "SELECT code, name, start_date, end_date FROM
		// match_round WHERE code<>0 AND code NOT IN (SELECT DISTINCT
		// IFNULL(round_code,0) FROM football_match WHERE id_home_team IN (?,?)
		// OR id_away_team IN (?,?)) ORDER BY name ";
		String query = "SELECT code, name, start_date, end_date, (SELECT COUNT(id_match) FROM football_match WHERE round_code=code) AS matchNo FROM match_round WHERE code<>0 AND code NOT IN (SELECT DISTINCT IFNULL(round_code,0) FROM football_match WHERE id_home_team IN (?,?) OR id_away_team IN (?,?)) ORDER BY matchNo DESC, name ";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query)) {

			stmt.setInt(1, homeTeamId);
			stmt.setInt(2, awayTeamId);
			stmt.setInt(3, homeTeamId);
			stmt.setInt(4, awayTeamId);

			try (ResultSet rs = stmt.executeQuery()) {

				rs.next();
				matchRoundData.add(new MatchRound(rs.getInt("code"), rs.getString("name"),
						rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate(),
						rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate()));
				int maxMatchRo = rs.getInt("matchNo");

				while (rs.next()) {
					if (maxMatchRo != rs.getInt("matchNo"))
						break;
					MatchRound round = new MatchRound();
					round.setCode(rs.getInt("code"));
					round.setName(rs.getString("name"));
					Date startDate = rs.getDate("start_date");
					round.setStartDate(startDate == null ? null : startDate.toLocalDate());
					Date endDate = rs.getDate("end_date");
					round.setEndDate(endDate == null ? null : endDate.toLocalDate());
					matchRoundData.add(round);
				}
			}
		} catch (Exception e) {
			throw new MatchDAOException(e);
		}

		return matchRoundData;
	}

	public static ObservableList<MatchRound> getMatchRoundListOld(int homeTeamId, int awayTeamId)
			throws MatchDAOException {
		ObservableList<MatchRound> matchRoundData = FXCollections.observableArrayList();

		String query = "SELECT code, name, start_date, end_date FROM match_round WHERE code<>0 AND code NOT IN (SELECT DISTINCT IFNULL(round_code,0) FROM football_match WHERE id_home_team IN (?,?) OR id_away_team IN (?,?)) ORDER BY name ";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query)) {

			stmt.setInt(1, homeTeamId);
			stmt.setInt(2, awayTeamId);
			stmt.setInt(3, homeTeamId);
			stmt.setInt(4, awayTeamId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					MatchRound round = new MatchRound();
					round.setCode(rs.getInt("code"));
					round.setName(rs.getString("name"));
					Date startDate = rs.getDate("start_date");
					round.setStartDate(startDate == null ? null : startDate.toLocalDate());
					Date endDate = rs.getDate("end_date");
					round.setEndDate(endDate == null ? null : endDate.toLocalDate());
					matchRoundData.add(round);
				}
			}
		} catch (Exception e) {
			throw new MatchDAOException(e);
		}

		return matchRoundData;
	}

	public static void updateMatch(int matchId, int roundCode, LocalDate matchDate) throws MatchDAOException {
		String query = "UPDATE football_match SET round_code = ?, match_date = ?, match_status_code = ? WHERE id_match = ?";
		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement st = co.prepareStatement(query)) {
			st.setInt(1, roundCode);
			st.setDate(2, matchDate == null ? null : Date.valueOf(matchDate));
			st.setString(3, ApplicationData.MATCH_PENDING);
			st.setInt(4, matchId);
			st.executeUpdate();
		} catch (Exception e) {
			throw new MatchDAOException(e);
		}
	}

	public static void saveMatchResults(int matchId, int awayScore, int homeScore, ObservableList<Contract> scorers)
			throws MatchDAOException {
		String query = "UPDATE football_match SET nr_goals_home_team=?, nr_goals_away_team=?, match_status_code = ? WHERE id_match = ? ";
		String insert = "INSERT INTO scorers VALUES (?,?)";

		try (Connection co = DBConnectionPool.getInstance().getConnection()) {
			co.setAutoCommit(false);
			try (PreparedStatement stmt = co.prepareStatement(query)) {
				stmt.setInt(1, homeScore);
				stmt.setInt(2, awayScore);
				stmt.setString(3, ApplicationData.MATCH_FINISHED);
				stmt.setInt(4, matchId);
				stmt.executeUpdate();
			}

			try (PreparedStatement stmt = co.prepareStatement(insert)) {
				for (Contract scorer : scorers) {
					stmt.setInt(1, matchId);
					stmt.setInt(2, scorer.getPlayer() == null ? null : scorer.getPlayer().getPlayerId());
					stmt.addBatch();
				}
				stmt.executeBatch();
				co.commit();
			} catch (SQLException e) {
				co.rollback();
				throw new MatchDAOException(e);
			} finally {
				co.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new MatchDAOException(e);
		}
	}

}
