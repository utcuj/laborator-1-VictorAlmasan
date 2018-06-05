package league.project.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
import java.time.LocalDate;

import org.apache.commons.lang.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import league.project.exception.ContractDAOException;
import league.project.exception.DuplicateEntryException;
import league.project.factory.ModelFactory;
import league.project.util.ApplicationData;

public class ContractDAO {

	public static ObservableList<Contract> getContractList(String teamNameField, String playerNameField)
			throws ContractDAOException {
		ObservableList<Contract> contractsData = FXCollections.observableArrayList();

		boolean firstCriteria = (StringUtils.isBlank(teamNameField) ? true : false);
		boolean secondCriteria = (StringUtils.isBlank(playerNameField) ? true : false);

		StringBuilder query = new StringBuilder(
				"SELECT c.id_contract, c.id_player, c.id_team, c.status_code, c.player_role_code, c.start_date, c.end_date, c.salary, p.cnp, p.first_name, p.last_name, p.nationality_code, p.date_of_birth, p.height, p.weight, p.player_position_code, t.team_name, t.team_short_name, pr.player_role,n.nationality,pp.player_position FROM contract c INNER JOIN team t ON c.id_team=t.id_team INNER JOIN player p ON c.id_player=p.id_player INNER JOIN player_role pr ON c.player_role_code = pr.player_role_code INNER JOIN nationality n ON p.nationality_code = n.nationality_code INNER JOIN player_position pp ON p.player_position_code = pp.player_position_code WHERE  1=1 ");
		if (!firstCriteria) {
			query.append(" AND UPPER(t.team_name) LIKE UPPER(?) ");
		}
		if (!secondCriteria) {
			query.append(" AND UPPER(p.last_name) LIKE UPPER(?) ");
		}
		query.append("ORDER by c.status_code, t.team_name, p.last_name ");

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query.toString())) {

			int i = 1;
			if (!firstCriteria) {
				stmt.setString(i, "%" + teamNameField.trim() + "%");

				i++;
			}
			if (!secondCriteria) {
				stmt.setString(i, "%" + playerNameField.trim() + "%");
			}

			ModelFactory modelFactory = new ModelFactory();
			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {

					int contractId = rs.getInt("c.id_contract");
					int playerId = rs.getInt("c.id_player");
					int teamId = rs.getInt("c.id_team");
					String statusCode = rs.getString("c.status_code");
					String playerRoleCode = rs.getString("c.player_role_code");
					Date startDate = rs.getDate("c.start_date");
					Date endDate = rs.getDate("c.end_date");
					int salary = rs.getInt("c.salary");
					String playerCnp = rs.getString("p.cnp");
					String firstName = rs.getString("p.first_name");
					String lastName = rs.getString("p.last_name");
					String nationalityCode = rs.getString("p.nationality_code");
					Date dateOfBirth = rs.getDate("p.date_of_birth");
					int height = rs.getInt("p.height");
					int weight = rs.getInt("p.weight");
					String playerPositionCode = rs.getString("p.player_position_code");
					String teamName = rs.getString("t.team_name");
					String teamShortName = rs.getString("t.team_short_name");
					String playerRoleName = rs.getString("pr.player_role");
					String nationalityName = rs.getString("n.nationality");
					String playerPositionName = rs.getString("pp.player_position");

					Nationality nationality = (Nationality) modelFactory.getModel("nationality");
					nationality.setNationalityCode(nationalityCode);
					nationality.setNationality(nationalityName);
					

					PlayerPosition playerPosition = (PlayerPosition) modelFactory.getModel("playerposition"); 
					playerPosition.setPlayerPositionCode(playerPositionCode);
					playerPosition.setPlayerPosition(playerPositionName);
					

					Player player = (Player) modelFactory.getModel("player");
					player.setPlayerId(playerId);
					player.setPlayerCnp(playerCnp);
					player.setFirstName(firstName);
					player.setLastName(lastName);
					player.setDateOfBirth(dateOfBirth == null ? null : dateOfBirth.toLocalDate());
					player.setHeight(height);
					player.setWeight(weight);
					player.setNationality(nationality);
					player.setPlayerPosition(playerPosition);

					Team team = (Team) modelFactory.getModel("team");
					team.setTeamId(teamId);
					team.setTeamName(teamName);
					team.setShortName(teamShortName);

					PlayerRole playerRole = (PlayerRole) modelFactory.getModel("playerrole");
					playerRole.setPlayerRoleCode(playerRoleCode);
					playerRole.setPlayerRole(playerRoleName);

					Contract contract = new Contract(contractId, player, team, statusCode, playerRole,
							startDate == null ? null : startDate.toLocalDate(),
							endDate == null ? null : endDate.toLocalDate(), salary, new ImageView(new Image(
									ApplicationData.CONTRACT_ACTIVE.equalsIgnoreCase(statusCode) ? "/images/greenC.png"
											: ApplicationData.CONTRACT_BANKRUPTCY.equalsIgnoreCase(statusCode)
													? "/images/redC.png" : "/images/orangeC.png")));

					contractsData.add(contract);
				}
			}

		} catch (Exception e) {
			throw new ContractDAOException(e);
		}

		return contractsData;
	}

	public static ObservableList<Team> getTeamsList() throws ContractDAOException {
		ObservableList<Team> teamsData = FXCollections.observableArrayList();

		String query = "SELECT id_team, team_name, team_short_name FROM team WHERE team_status_code = ? ORDER BY team_name";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query)) {
			stmt.setString(1, ApplicationData.TEAM_ACTIVE);
			
			ModelFactory modelFactory = new ModelFactory();

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int teamId = rs.getInt("id_team");
					String teamName = rs.getString("team_name");
					String teamShortName = rs.getString("team_short_name");
					Team team = (Team) modelFactory.getModel("team");
					team.setTeamId(teamId);
					team.setTeamName(teamName);
					team.setShortName(teamShortName);
					teamsData.add(team);
				}

			}
		} catch (Exception e) {
			throw new ContractDAOException(e);
		}

		return teamsData;
	}

	public static ObservableList<PlayerRole> getPlayerRolesList() throws ContractDAOException {
		ObservableList<PlayerRole> playerRolesData = FXCollections.observableArrayList();

		String query = "SELECT player_role_code, player_role FROM player_role ORDER BY player_role";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query)) {
			
			ModelFactory modelFactory = new ModelFactory();
			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					PlayerRole playerRole = (PlayerRole) modelFactory.getModel("playerrole");
					playerRole.setPlayerRoleCode(rs.getString("player_role_code"));
					playerRole.setPlayerRole(rs.getString("player_role"));
					playerRolesData.add(playerRole);
				}
			}
		} catch (Exception e) {
			throw new ContractDAOException(e);
		}

		return playerRolesData;
	}

	public static ObservableList<Player> getPlayersList() throws ContractDAOException {
		ObservableList<Player> playersData = FXCollections.observableArrayList();

		String query = "SELECT id_player, cnp, first_name, last_name,player_position_code FROM player WHERE id_player NOT IN (SELECT DISTINCT IFNULL(id_player,0) FROM contract) ORDER BY last_name, first_name";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query)) {
			
			ModelFactory modelFactory = new ModelFactory();
			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Player player = (Player) modelFactory.getModel("player");
					player.setPlayerId(rs.getInt("id_player"));
					player.setPlayerCnp(rs.getString("cnp"));
					player.setFirstName(rs.getString("first_name"));
					player.setLastName(rs.getString("last_name"));
					player.setPlayerPosition(new PlayerPosition(rs.getString("player_position_code"),null));
					playersData.add(player);
				}
			}
		} catch (Exception e) {
			throw new ContractDAOException(e);
		}

		return playersData;
	}

	public static ObservableList<Nationality> getNationalitiesList() throws ContractDAOException {
		ObservableList<Nationality> nationalitiesData = FXCollections.observableArrayList();

		String query = "SELECT nationality_code, nationality FROM nationality ORDER BY nationality";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query)) {

			ModelFactory modelFactory = new ModelFactory();
			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Nationality nationality = (Nationality) modelFactory.getModel("nationality");
					nationality.setNationalityCode(rs.getString("nationality_code"));
					nationality.setNationality(rs.getString("nationality"));
					nationalitiesData.add(nationality);
				}
			}
		} catch (Exception e) {
			throw new ContractDAOException(e);
		}

		return nationalitiesData;
	}

	public static ObservableList<PlayerPosition> getPlayerPositionsList() throws ContractDAOException {
		ObservableList<PlayerPosition> playerPositionsData = FXCollections.observableArrayList();

		String query = "SELECT player_position_code, player_position FROM player_position ORDER BY player_position";

		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement stmt = co.prepareStatement(query)) {

			ModelFactory modelFactory = new ModelFactory();

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					PlayerPosition playerPosition = (PlayerPosition) modelFactory.getModel("playerposition");
					playerPosition.setPlayerPositionCode(rs.getString("player_position_code"));
					playerPosition.setPlayerPosition(rs.getString("player_position"));
					playerPositionsData.add(playerPosition);
				}
			}
		} catch (Exception e) {
			throw new ContractDAOException(e);
		}

		return playerPositionsData;
	}

	public static void renewContract(int constractId, LocalDate startDate, LocalDate endDate, PlayerRole role,
			int salary) throws ContractDAOException {
		String query = "{CALL renewContract(?,?,?,?,?)}";
		try (Connection co = DBConnectionPool.getInstance().getConnection()) {
			co.setAutoCommit(false);
			try (CallableStatement stmt = co.prepareCall(query)) {

				stmt.setInt(1, constractId);
				stmt.setDate(2, java.sql.Date.valueOf(startDate));
				stmt.setDate(3, java.sql.Date.valueOf(endDate));
				stmt.setString(4, role.getPlayerRoleCode());
				stmt.setInt(5, salary);
				stmt.execute();
				co.commit();
			} catch (SQLException e) {
				co.rollback();
				throw new ContractDAOException(e);
			} finally {
				co.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new ContractDAOException(e);
		}
	}

	public static boolean deleteContract(int contractId) throws ContractDAOException {
		String query = "UPDATE contract SET status_code = ? WHERE id_contract = ?";
		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement st = co.prepareStatement(query)) {
			st.setString(1, ApplicationData.CONTRACT_CANCELLED);
			st.setInt(2, contractId);
			if (st.executeUpdate() > 0) {
				return true;
			}
		} catch (Exception e) {
			throw new ContractDAOException(e);
		}
		return false;
	}

	public static int saveContract(Contract contract) throws DuplicateEntryException, ContractDAOException {
		int contractId = 0;
		String query = "{CALL saveContract(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		try (Connection co = DBConnectionPool.getInstance().getConnection()) {
			co.setAutoCommit(false);
			try (CallableStatement stmt = co.prepareCall(query)) {
				Player player = contract.getPlayer();
				stmt.setInt(1, player.getPlayerId());
				stmt.setInt(2, contract.getTeam() == null ? null : contract.getTeam().getTeamId());
				stmt.setString(3,
						contract.getPlayerRole() == null ? null : contract.getPlayerRole().getPlayerRoleCode());
				stmt.setDate(4, Date.valueOf(contract.getStartDate()));
				stmt.setDate(5, Date.valueOf(contract.getEndDate()));
				stmt.setInt(6, contract.getSalary());
				stmt.setString(7, player.getPlayerCnp());
				stmt.setString(8, player.getNationality() == null ? null
						: contract.getPlayer().getNationality().getNationalityCode());
				stmt.setString(9, player.getFirstName());
				stmt.setString(10, player.getLastName());
				stmt.setDate(11, player.getDateOfBirth() == null ? null : Date.valueOf(player.getDateOfBirth()));
				stmt.setInt(12, player.getHeight());
				stmt.setInt(13, player.getWeight());
				stmt.setString(14, player.getPlayerPosition() == null ? null
						: contract.getPlayer().getPlayerPosition().getPlayerPositionCode());
				stmt.registerOutParameter(15, Types.INTEGER);
				stmt.execute();

				contractId = (Integer) stmt.getObject(15, Integer.class);
				co.commit();
				return contractId;
			} catch (SQLIntegrityConstraintViolationException e) {
				co.rollback();
				throw new DuplicateEntryException(e);
			} catch (SQLException e) {
				co.rollback();
				throw new ContractDAOException(e);
			} finally {
				co.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new ContractDAOException(e);
		}
	}

}
