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
import league.project.exception.ApplicationDAOException;
import league.project.exception.DuplicateEntryException;
import league.project.exception.TeamDAOException;
import league.project.model.ApplicationDAO;
import league.project.model.Coach;
import league.project.model.Team;
import league.project.model.TeamDAO;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;
import league.project.util.DateUtil;

public class TeamsOverviewController {

	@FXML
	private TableView<Team> teamsTable;
	@FXML
	private TableColumn<Team, String> teamNameColumn;
	@FXML
	private TableColumn<Team, Coach> headCouchColumn;
	@FXML
	private TableColumn<Team, String> groundColumn;
	@FXML
	private TableColumn<Team, ImageView> statusColumn;

	@FXML
	private Label idLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private Label shortNameLabel;
	@FXML
	private Label coachLabel;
	@FXML
	private Label budgetLabel;
	@FXML
	private Label groundLabel;
	@FXML
	private Label foundedLabel;

	@FXML
	private SplitPane verticalSplitPane;

	@FXML
	private TextField teamNameField;
	@FXML
	private TextField headCoachField;
	@FXML
	private TextField groundField;

	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public TeamsOverviewController() {
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		// Initialize the teamsTable with the four columns.
		teamNameColumn.setCellValueFactory(cellData -> cellData.getValue().teamNameProperty());
		headCouchColumn.setCellValueFactory(cellData -> cellData.getValue().headCoachProperty());
		groundColumn.setCellValueFactory(cellData -> cellData.getValue().groundProperty());
		statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusImgProperty());

		// Clear team details.
		showTeamInformation(null);

		// Clear search filds
		setSearchFields();

		// Listen for selection changes in table and show the team details when
		// changed.
		teamsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showTeamInformation(newValue));
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	private void setSearchFields() {
		teamNameField.setText("");
		headCoachField.setText("");
		groundField.setText("");
	}

	/**
	 * Fills all text fields to show information about the team. If the
	 * specified team is null, all labels are cleared.
	 *
	 * @param team
	 *            the team or null
	 */
	private void showTeamInformation(Team team) {
		if (team != null) {
			// Fill the labels with info from the team object.
			idLabel.setText(Integer.toString(team.getTeamId()));
			budgetLabel.setText(Integer.toString(team.getBudget()));
			nameLabel.setText(team.getTeamName());
			shortNameLabel.setText(team.getShortName());
			coachLabel.setText(team.getHeadCoach().getCoachName());
			groundLabel.setText(team.getGround());
			foundedLabel.setText(DateUtil.format(team.getFounded()));
		} else {
			// team is null, remove all the text.
			idLabel.setText("");
			budgetLabel.setText("");
			nameLabel.setText("");
			shortNameLabel.setText("");
			coachLabel.setText("");
			groundLabel.setText("");
			foundedLabel.setText("");
		}
	}

	/**
	 * Called when the user clicks the Search button.
	 */
	@FXML
	private void handleSearchTeam() {
		try {
			teamsTable.setItems(
					TeamDAO.getTeamList(teamNameField.getText(), headCoachField.getText(), groundField.getText()));
			// no row selected
			if (teamsTable.getItems().isEmpty()) {
				AlertDialog.showInformation("Search Team", "Your search yielded no results!");
			}
		} catch (TeamDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
		} catch (Exception e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
		}
	}

	/**
	 * Called when the user clicks the new button. Opens a dialog to edit
	 * details for a new team.
	 */
	@FXML
	private void handleNewTeam() {
		try {
			if (ApplicationDAO.isSeasonStarted().getStatus() == 1)
				AlertDialog.showInformation("League Information", "When the season is started, teams cannot be added!");
			else {
				Team tempTeam = new Team(0, ApplicationData.TEAM_BUDGET_MIN, "", "", null, "", null, ApplicationData.TEAM_ACTIVE, null);
				boolean okClicked = showTeamEditDialog(tempTeam, "New Team");

				if (okClicked) {
					try {
						int newId = TeamDAO.insertTeam(tempTeam);
						if (newId > 0) {
							tempTeam.setTeamId(newId);
							teamsTable.getItems().add(tempTeam);
							AlertDialog.showSuccess("Success", tempTeam.getTeamName() + " team was saved!");
						}
					} catch (TeamDAOException e) {
						AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
					}catch(DuplicateEntryException e){
						AlertDialog.showError("Duplicate Team", "Team name and team short name must be unique!");
					}
				}
			}
		} catch (ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
		} catch (Exception e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected team.
	 */
	@FXML
	private void handleEditTeam() {
		try {
			Team selectedTeam = teamsTable.getSelectionModel().getSelectedItem();
			System.out.println(selectedTeam.getTeamName());
			if (selectedTeam != null) {
				System.out.println("1");
				if (ApplicationDAO.isSeasonStarted().getStatus() == 1
						&& ApplicationData.TEAM_INACTIVE.equalsIgnoreCase(selectedTeam.getStatus())) {
					System.out.println("2");
					AlertDialog.showInformation("League Information",
							"When the season is started, inactive teams cannot be modified!");
				}
				else {
					System.out.println("3");
					boolean okClicked = showTeamEditDialog(selectedTeam, "Edit Team");
					if (okClicked) {
						System.out.println("4");
						showTeamInformation(selectedTeam);
						AlertDialog.showSuccess("Edit Team", "Team " + selectedTeam.getTeamName() + " was updated!");
					}
				}

			} else
				AlertDialog.showInformation("Input Validation", "Select a team!");
			System.out.println("5");
		} catch (ApplicationDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
		} catch (Exception e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
		}
	}

	@FXML
	private void handleReset() {
		showTeamInformation(null);
		teamsTable.getItems().clear();
		teamNameField.setText("");
		headCoachField.setText("");
		groundField.setText("");
	}

	/**
	 * Opens a dialog to edit details for the specified team. If the user clicks
	 * OK, the changes are saved into the provided team object and true is
	 * returned.
	 *
	 * @param team
	 *            the team object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showTeamEditDialog(Team team, String headerDialog) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/TeamEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			System.out.println("6");
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle(headerDialog);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setResizable(false);
			dialogStage.setHeight(320);
			dialogStage.setWidth(340);

			System.out.println("7");
			// Set the team into the controller.
			TeamEditDialogController controller = loader.getController();
			System.out.println("7.1");
			controller.setDialogStage(dialogStage);
			System.out.println("7.2");
			controller.setTeam(team);
			System.out.println("7.3");
			if ("New Team".equalsIgnoreCase(headerDialog)) {
				controller.getStatusCheckBox().setSelected(true);
				controller.getStatusCheckBox().setDisable(true);
			}
			
			System.out.println("8");
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			System.out.println("9");
			AlertDialog.showError("System Error",
					"There was an error (" + e.getMessage() + "). Contact the system administrator if this problem persists!");
			System.out.println("10");
			return false;
		}
	}

}
