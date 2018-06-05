package league.project.controller;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import league.project.MainApp;
import league.project.exception.ApplicationDAOException;
import league.project.exception.ContractDAOException;
import league.project.exception.UserLoginException;
import league.project.model.Application;
import league.project.model.ApplicationDAO;
import league.project.model.PlayerPosition;
import league.project.model.UserDAO;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;
import league.reporting.ContractReports;
import league.reporting.MatchReports;
import league.reporting.PlayerReports;
import league.reporting.TeamReports;

public class MainLayoutController {

	private MainApp mainApp;

	@FXML
	private Menu m1;
	@FXML
	private Menu m2;
	@FXML
	private Menu m3;
	@FXML
	private MenuItem mi22;

	public MainLayoutController() {

	}

	@FXML
	private void initialize() {
		setMenuDisabled(true, true);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void quitApplication(ActionEvent event) {
		Platform.exit();
		System.exit(0);
	}

	public void showTeamsOverview(ActionEvent event) {
		try {
			// Load TeamsOverview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/TeamsOverview.fxml"));
			AnchorPane teamsOverview = (AnchorPane) loader.load();

			// Set TeamsOverview into the center of root layout.
			mainApp.getRootLayout().setCenter(teamsOverview);

			// Give the controller access to the main app.
			TeamsOverviewController controller = loader.getController();
			controller.setMainApp(mainApp);

		} catch (IOException e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e + "). Contact the system administrator if this problem persists!");
		}
	}

	public void showContractsOverview(ActionEvent event) {
		try {
			// Load ContractsOverview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ContractsOverview.fxml"));
			AnchorPane contractsOverview = (AnchorPane) loader.load();

			// Set contractsOverview into the center of root layout.
			mainApp.getRootLayout().setCenter(contractsOverview);

			// Give the controller access to the main app.
			ContractsOverviewController controller = loader.getController();
			controller.setMainApp(mainApp);

		} catch (IOException e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e + "). Contact the system administrator if this problem persists!");
		}
	}

	public void showLeagueManagementDialog(ActionEvent event) {
		mainApp.getRootLayout().setCenter(null);
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/LeagueManagement.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Application");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setResizable(false);
			dialogStage.setScene(scene);

			LeagueManagementController controller = loader.getController();
			Application app = new Application();
			controller.setApplication(app);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

		} catch (IOException e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e + "). Contact the system administrator if this problem persists!");
		}
	}

	public void showScheduleCreateOverview(ActionEvent event) {
		try {
			if (ApplicationDAO.isSeasonStarted().getStatus() == 1) {
				try {
					// Load ContractsOverview.
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(MainApp.class.getResource("view/ScheduleCreateOverview.fxml"));
					AnchorPane scheduleCreateOverview = (AnchorPane) loader.load();

					// Set contractsOverview into the center of root layout.
					mainApp.getRootLayout().setCenter(scheduleCreateOverview);

					// Give the controller access to the main app.
					ScheduleCreateOverviewController controller = loader.getController();
					controller.setMainApp(mainApp);

				} catch (IOException e) {
					AlertDialog.showError("System Error", "There was an error (" + e
							+ "). Contact the system administrator if this problem persists!");
				}
			} else {
				AlertDialog.showInformation("League Information", "League season not started!");
			}
		} catch (ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e);
		} catch (Exception e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e + "). Contact the system administrator if this problem persists!");
		}
	}

	public void showLoginDialog(ActionEvent event) {
		mainApp.getRootLayout().setCenter(null);
		setMenuDisabled(true, true);
		Optional<Pair<String, String>> result = AlertDialog.loginDialog();

		if (result.isPresent()) {
			String username = result.get().getKey();
			String password = result.get().getValue();

			if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
				try {
					if (UserDAO.login(username, password)) {
						switch (username) {
						case ApplicationData.USER_ADMIN:
							setMenuDisabled(false, false);
							break;
						case ApplicationData.USER_USER:
							setMenuDisabled(true, false);
							break;
						default: 
							setMenuDisabled(true, false);
							break;
						}
						AlertDialog.showSuccess("Authentication successful",
								"You are now logged in as " + username.toUpperCase() + ".");
					} else {
						setMenuDisabled(true, true);
						AlertDialog.showError("Login Failed", "Username or password is incorrect!");
					}
				} catch (UserLoginException e) {
					AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
				}
			} else {
				setMenuDisabled(true, true);
				AlertDialog.showError("Input Validation", "Username and password fields are mandatory!");
			}
		} else
			setMenuDisabled(true, true);
	}

	private void setMenuDisabled(boolean status1, boolean status2) {
		m1.setDisable(status1);
		m2.setDisable(status1);
		m3.setDisable(status2);
	}

	public void generateTeamOverall(ActionEvent event) {
		mainApp.getRootLayout().setCenter(null);
		try {
			if (ApplicationDAO.isSeasonStarted().getStatus() == ApplicationData.SEASON_NOT_STARTED)
				AlertDialog.showInformation("League Information", "The season is not started!");
			else
				TeamReports.teamStandings("teamsOverall");
		} catch (ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e);
		}
	}

	public void generateTeamHome(ActionEvent event) {
		mainApp.getRootLayout().setCenter(null);
		try {
			if (ApplicationDAO.isSeasonStarted().getStatus() == ApplicationData.SEASON_NOT_STARTED)
				AlertDialog.showInformation("League Information", "The season is not started!");
			else
				TeamReports.teamStandings("teamsHome");
		} catch (ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e);
		}
	}

	public void generateTeamAway(ActionEvent event) {
		mainApp.getRootLayout().setCenter(null);

		try {
			if (ApplicationDAO.isSeasonStarted().getStatus() == ApplicationData.SEASON_NOT_STARTED)
				AlertDialog.showInformation("League Information", "The season is not started!");
			else
				TeamReports.teamStandings("teamsAway");
		} catch (ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e);
		}
	}

	public void generateScorers(ActionEvent event) {
		mainApp.getRootLayout().setCenter(null);
		try {
			if (ApplicationDAO.isSeasonStarted().getStatus() == ApplicationData.SEASON_NOT_STARTED)
				AlertDialog.showInformation("League Information", "The season is not started!");
			else
				PlayerReports.playerStandings();
		} catch (ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e);
		}
	}

	public void generateScorersByPosition(ActionEvent event) {
		mainApp.getRootLayout().setCenter(null);
		try {
			if (ApplicationDAO.isSeasonStarted().getStatus() == ApplicationData.SEASON_NOT_STARTED)
				AlertDialog.showInformation("League Information", "The season is not started!");
			else{
			PlayerPosition pos = AlertDialog.choiceDialog();
			if (pos != null)
				PlayerReports.playerStandingsByPosition(pos.getPlayerPositionCode());
			}
		} catch (ContractDAOException | ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e);
		}
	}

	public void generateMatchList() {
		mainApp.getRootLayout().setCenter(null);
		try {
			if (ApplicationDAO.isSeasonStarted().getStatus() == ApplicationData.SEASON_NOT_STARTED)
				AlertDialog.showInformation("League Information", "The season is not started!");
			else
				MatchReports.matchList();
		} catch (ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e);
		}
	}

	public void generatePlayerContractHistory() {
		mainApp.getRootLayout().setCenter(null);
		ContractReports.playerContractHistory();
	}

	public void showAbout(ActionEvent event) {
		AlertDialog.showAbout();
	}
}
