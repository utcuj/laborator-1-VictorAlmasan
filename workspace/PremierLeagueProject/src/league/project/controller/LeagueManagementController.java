package league.project.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import league.project.MainApp;
import league.project.exception.ApplicationDAOException;
import league.project.model.Application;
import league.project.model.ApplicationDAO;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;

public class LeagueManagementController {

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private TitledPane tit;

	@FXML
	private DatePicker startDId;

	@FXML
	private Button startB;

	@FXML
	private Button createB;

	private MainApp mainApp;
	private Application app;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public LeagueManagementController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		try {
			Application app = ApplicationDAO.isSeasonStarted();
			if (app.getStatus() == 1) {
				startDId.setValue(app.getStartDate());
				startDId.setDisable(true);
				startB.setDisable(true);
				createB.setDisable(false);
			} else {
				startDId.setDisable(false);
				startB.setDisable(false);
				createB.setDisable(true);
			}
		} catch (ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
		} catch (Exception e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
		}

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setApplication(Application app) {
		this.app = app;
	}

	@FXML
	private void handleStartLeague() {
		if (isInputValid()) {
			app.setStartDate(startDId.getValue());
			try {
				switch (ApplicationDAO.startSeason(app, ApplicationData.TEAM_PLAYER_NO)) {
				case 0:
					AlertDialog.showError("Team Error", "Number of active teams should be greater than 0 and even.");
					break;
				case 1:
					startDId.setDisable(true);
					startB.setDisable(true);
					createB.setDisable(false);
					AlertDialog.showSuccess("Success", "Season started!");
					break;
				case 2:
					AlertDialog.showError("Team Error", "Active teams do not have enough players!");
					break;
				default:
					AlertDialog.showError("System Error", "Someting went wrong!.");
					break;
				}
			} catch (ApplicationDAOException e) {
				AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
			} catch (Exception e) {
				AlertDialog.showError("System Error",
						"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
			}
		}
	}

	@FXML
	private void handleClearLeague() {
		Optional<ButtonType> result = AlertDialog.showConfirmationDialog("Irreversible Action",
				"Are you sure you want to continue?");
		if (result.get() == ButtonType.OK) {

			try {
				ApplicationDAO.newSeason();
				startDId.setValue(null);
				startDId.setDisable(false);
				startB.setDisable(false);
				createB.setDisable(true);
				AlertDialog.showSuccess("Success", "You can start to create a new season.");
			} catch (ApplicationDAOException e) {
				AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
			} catch (Exception e) {
				AlertDialog.showError("System Error",
						"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
			}
		}

	}

	private boolean isInputValid() {
		StringBuilder errorMessage = new StringBuilder();

		LocalDate startDate = startDId.getValue();
		if (startDate == null) {
			errorMessage.append("Start Date field is mandatory!\n");
		} else if (startDate.isBefore(LocalDate.now())) {
			errorMessage.append("No valid start date!\n");
		} else if (DayOfWeek.FRIDAY != startDate.getDayOfWeek()) {
			errorMessage.append("The season should start Friday!\n");
		}

		if (errorMessage.toString().length() == 0) {
			return true;
		} else {
			AlertDialog.showError("Input Validation", errorMessage.toString());
			return false;
		}
	}

}
