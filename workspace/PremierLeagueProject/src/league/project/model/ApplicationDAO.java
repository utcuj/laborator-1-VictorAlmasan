package league.project.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import league.project.exception.ApplicationDAOException;

public class ApplicationDAO {

	/**
	 * Check application status. If season is started, status=1.
	 *
	 * @return
	 */
	public static Application isSeasonStarted() throws ApplicationDAOException {
		String query = "SELECT status, code, start_date FROM application WHERE code='S'";
		Application app = new Application();
		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement st = co.prepareStatement(query)) {
			try (ResultSet rs = st.executeQuery()) {
				rs.next();
				app.setCode(rs.getString("code"));
				app.setStatus(rs.getInt("status"));
				Date date = rs.getDate("start_date");
				app.setStartDate(date == null ? null : date.toLocalDate());
			}
		} catch (Exception e) {
			throw new ApplicationDAOException(e);
		}
		return app;
	}

	/**
	 * Starts league season and populates all possible matches between active
	 * teams. The number of teams should be even and to have enough contracts.
	 *
	 * @param app
	 * @param minPlayerNo
	 * @return
	 * @throws ApplicatioDAOException
	 */
	public static int startSeason(Application app, int minPlayerNo) throws ApplicationDAOException {
		int status = 0;
		String query = "{CALL startSeason(?,?,?)}";
		try (Connection co = DBConnectionPool.getInstance().getConnection()) {
			co.setAutoCommit(false);
			try (CallableStatement stmt = co.prepareCall(query)) {

				stmt.setDate(1, java.sql.Date.valueOf(app.getStartDate()));
				stmt.setInt(2, minPlayerNo);
				stmt.registerOutParameter(3, Types.INTEGER);
				stmt.execute();

				status = (Integer) stmt.getObject(3, Integer.class);

				co.commit();
				return status;
			} catch (SQLException e) {
				co.rollback();
				throw new ApplicationDAOException(e);
			} finally {
				co.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new ApplicationDAOException(e);
		}
	}

	/**
	 * Create a new season. All matches and scorers are deleted.
	 *
	 * @throws ApplicatioDAOException
	 */
	public static void newSeason() throws ApplicationDAOException {
		String query = "{ CALL newSeason() }";
		try (Connection co = DBConnectionPool.getInstance().getConnection()) {
			co.setAutoCommit(false);
			try (CallableStatement stmt = co.prepareCall(query)) {
				stmt.executeQuery();
				co.commit();
			} catch (SQLException e) {
				co.rollback();
				throw new ApplicationDAOException(e);
			} finally {
				co.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new ApplicationDAOException(e);
		}
	}

}
