package league.project.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import league.project.MainApp;
import league.project.exception.MatchDAOException;
import league.project.model.Match;
import league.project.model.MatchDAO;
import league.project.model.Team;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;
import league.project.util.DateUtil;

public class ScheduleCreateOverviewController {

	@FXML
	private TableView<Match> matchesTable;
	@FXML
	private TableColumn<Match, Team> homeTeamNameColumn;
	@FXML
	private TableColumn<Match, Team> awayTeamNameColumn;
	@FXML
	private TableColumn<Match, ImageView> statusColumn;

	@FXML
	private Label idMatchLabel;
	@FXML
	private Label homeTeamLabel;
	@FXML
	private Label awayTeamLabel;
	@FXML
	private Label groundLabel;
	@FXML
	private Label matchDateLabel;
	@FXML
	private Label roundLabel;
	@FXML
	private Label homeScoreLabel;
	@FXML
	private Label awayScoreLabel;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private SplitPane verticalSplitPane;

	@FXML
	private TextField homeTeamField;
	@FXML
	private TextField awayTeamField;

	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public ScheduleCreateOverviewController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		homeTeamNameColumn.setCellValueFactory(cellData -> cellData.getValue().homeTeamProperty());
		awayTeamNameColumn.setCellValueFactory(cellData -> cellData.getValue().awayTeamProperty());
		statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusImgProperty());

		// Clear match details.
		showMatchInformation(null);

		// Listen for selection changes in table and show the match details when
		// changed.
		matchesTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showMatchInformation(newValue));

		try {
			matchesTable.setItems(MatchDAO.getMatchesList());
		} catch (MatchDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
		}

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Fills all text fields to show information about the match. If the
	 * specified match is null, all labels are cleared.
	 *
	 * @param match
	 *            the match or null
	 */
	private void showMatchInformation(Match match) {
		if (match != null) {
			// Fill the labels with info from the match object.
			idMatchLabel.setText(String.valueOf(match.getMatchId()));
			homeTeamLabel.setText(match.getHomeTeam().getTeamName());
			awayTeamLabel.setText(match.getAwayTeam().getTeamName());
			groundLabel.setText(match.getHomeTeam().getGround());
			matchDateLabel.setText(match.getMatchDate() == null ? "" : DateUtil.format(match.getMatchDate()));
			roundLabel.setText(match.getRound().getName());
			homeScoreLabel.setText(String.valueOf(match.getNoGoalsHomeTeam()));
			awayScoreLabel.setText(String.valueOf(match.getNoGoalsAwayTeam()));
			// match.getMatchDate().isAfter(match.getMatchDate());

		} else {
			// match is null, remove all the text.
			idMatchLabel.setText("");
			homeTeamLabel.setText("");
			awayTeamLabel.setText("");
			groundLabel.setText("");
			matchDateLabel.setText("");
			roundLabel.setText("");
			homeScoreLabel.setText("");
			awayScoreLabel.setText("");
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected match scheduling.
	 */
	@FXML
	private void handleSchedule() {
		Match selectedMatch = matchesTable.getSelectionModel().getSelectedItem();
		if (selectedMatch != null) {
			if (ApplicationData.MATCH_PENDING.equalsIgnoreCase(selectedMatch.getMatchStatusCode())
					|| ApplicationData.MATCH_UNSCHEDULED.equalsIgnoreCase(selectedMatch.getMatchStatusCode())) {
				boolean okClicked = showMatchScheduleDialog(selectedMatch, "Match Schedule");
				if (okClicked) {
					showMatchInformation(selectedMatch);
				}
			} else {
				AlertDialog.showInformation("Attention", "Only unscheduled and pending matches can be scheduled!");
			}
		} else {
			AlertDialog.showInformation("Input Validation", "Select a football match!");
		}
	}

	@FXML
	private void handleResults() {
		Match selectedMatch = matchesTable.getSelectionModel().getSelectedItem();
		if (selectedMatch != null) {
			if (ApplicationData.MATCH_PENDING.equalsIgnoreCase(selectedMatch.getMatchStatusCode())) {
				boolean okClicked = showMatchResultsDialog(selectedMatch, "Match Results");
				if (okClicked) {
					showMatchInformation(selectedMatch);
				}
			} else {
				AlertDialog.showInformation("Attention", "Only pending matches can be updated!");
			}
		} else {
			AlertDialog.showInformation("Input Validation", "Select a football match!");
		}
	}

	@FXML
	private void handleReset() {
		showMatchInformation(null);
		matchesTable.getItems().clear();
		homeTeamField.setText("");
		awayTeamField.setText("");
	}

	/**
	 * Called when the user clicks the Search button.
	 */
	@FXML
	private void handleSearchMatch() {

		try {
			matchesTable.getItems().clear();
			matchesTable.setItems(MatchDAO.getMatchListCriteria(homeTeamField.getText(), awayTeamField.getText()));
			if (matchesTable.getItems().isEmpty()) {
				AlertDialog.showInformation("Search Match", "Your search yielded no results!");
			}
		} catch (MatchDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
		} catch (Exception e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
		}
	}

	public boolean showMatchScheduleDialog(Match match, String headerDialog) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MatchScheduleDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle(headerDialog);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setResizable(false);
			dialogStage.setHeight(230);
			dialogStage.setWidth(290);

			// Set the match into the controller.
			MatchScheduleDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMatchSchedule(match);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
			return false;
		}
	}

	public boolean showMatchResultsDialog(Match match, String headerDialog) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MatchResultsDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle(headerDialog);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setResizable(false);
			dialogStage.setHeight(480);
			dialogStage.setWidth(676);

			MatchResultsDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMatch(match);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
			return false;
		}
	}
}
