package test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import league.project.exception.ContractDAOException;
import league.project.exception.TeamDAOException;
import league.project.exception.UserLoginException;
import league.project.factory.ModelFactory;
import league.project.model.Coach;
import league.project.model.DBConnectionPool;
import league.project.model.Nationality;
import league.project.model.UserDAO;
import league.project.util.ApplicationData;

class TestModelFactory {

	@Test
	void testModelFactoryPentruCoach() {
		try {
			UserDAO.login("admin", "admin");
		} catch (UserLoginException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<Coach> coachesData = new ArrayList<>();
		String query = "SELECT coach_code, coach_name FROM coach;";
		try (Connection co = DBConnectionPool.getInstance().getConnection();
				PreparedStatement st = co.prepareStatement(query)) {
			ModelFactory modelFactory = new ModelFactory();
			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					Coach coach = (Coach) modelFactory.getModel("coach");
					coach.setCoachCode(rs.getString("coach_code"));
					coach.setCoachName(rs.getString("coach_name"));
					coachesData.add(coach);
				}
			}
			Coach c = coachesData.get(0);
			assertEquals("Massimiliano Allegri", c.getCoachName());
		} catch (Exception e) {
			System.out.println("Error");
		}
	}
	
	
	@Test
	void testModelFactoryPentruNationality() {
		String query = "SELECT nationality_code, nationality FROM nationality ORDER BY nationality";

		List<Nationality> nationalitiesData = new ArrayList<>();
		
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
			
			Nationality n = nationalitiesData.get(0);
			assertEquals("Argentina", n.getNationality());

		} catch (Exception e) {
			System.out.println("Error");
		}
	}

}
