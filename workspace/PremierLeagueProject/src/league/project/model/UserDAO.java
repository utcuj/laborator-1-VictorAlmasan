package league.project.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import league.project.exception.UserLoginException;

public class UserDAO {

	public static boolean login(String username, String password) throws UserLoginException {
		String query = "SELECT COUNT(*) AS count FROM user_league WHERE BINARY username=? AND password=SHA1(?)";
		try (Connection co = DBConnectionPool.getInstance(username, password).getConnection();
				PreparedStatement st = co.prepareStatement(query)) {
			st.setString(1, username);
			st.setString(2, password);
			try (ResultSet rs = st.executeQuery()) {
				rs.next();
				if (rs.getInt(1) == 1)
					return true;
			}
		} catch (Exception e) {
			throw new UserLoginException(e);
		}
		return false;
	}

}
