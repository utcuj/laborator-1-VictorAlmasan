package league.project.controller;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import league.project.exception.TeamDAOException;
import league.project.model.Coach;
import league.project.model.Team;
import league.project.model.TeamDAO;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;

/**
 * Dialog to edit details of a team.
 *
 */

public class TeamEditDialogController {

	@FXML
	private TextField budgetField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField shortNameField;
	@FXML
	private ComboBox<Coach> coachComboBox;
	@FXML
	private TextField groundField;
	@FXML
	private DatePicker foundedDatePicker;
	@FXML
	private CheckBox statusCheckBox;

	private Stage dialogStage;
	private Team team;
	private boolean okClicked = false;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		try {
			System.out.println("11");
			coachComboBox.setItems(TeamDAO.getCoachesList());
			System.out.println("12");
		} catch (TeamDAOException e) {
			AlertDialog.showError("Populate Coach List", "Operation failed because : " + e.getMessage());
		}
	}

	/**
	 * Sets the stage of this dialog.
	 *
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public static ObservableList<Coach> coachList() {
		return null;
	}

	public CheckBox getStatusCheckBox() {
		return this.statusCheckBox;
	}

	/**
	 * Sets the team to be edited in the dialog.
	 *
	 * @param team
	 */
	public void setTeam(Team team) {
		this.team = team;
		System.out.println("7.2.2");
		budgetField.setText(Integer.toString(team.getBudget()));
		System.out.println("7.2.3");
		nameField.setText(team.getTeamName());
		System.out.println("7.2.4");
		shortNameField.setText(team.getShortName());
		System.out.println("7.2.5");

		if (team.getHeadCoach() != null && ApplicationData.TEAM_ACTIVE.equalsIgnoreCase(team.getStatus()))
			coachComboBox.getItems().add(team.getHeadCoach());
		System.out.println("7.2.6");
		if (coachComboBox.getItems().contains(team.getHeadCoach()))
			coachComboBox.setValue(team.getHeadCoach());
		System.out.println("7.2.7");
		groundField.setText(team.getGround());
		System.out.println("7.2.8");
		foundedDatePicker.setValue(team.getFounded());
		System.out.println("7.2.9");
		System.out.println(team.getStatus());
		if (team.getStatus().equals(ApplicationData.TEAM_ACTIVE)) {
			statusCheckBox.setSelected(true);
			System.out.println("7.2.10");
		} else {
			statusCheckBox.setSelected(false);
			System.out.println("7.2.11");
		}
		System.out.println("7.2.12");
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 *
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {

		if (isInputValid()) {
			try {
				Team tempTeam = new Team(team.getTeamId(), Integer.parseInt(budgetField.getText()), nameField.getText(),
						shortNameField.getText(), coachComboBox.getValue(), groundField.getText(),
						foundedDatePicker.getValue(),
						statusCheckBox.isSelected() ? ApplicationData.TEAM_ACTIVE : ApplicationData.TEAM_INACTIVE,
						null);
				TeamDAO.editTeam(tempTeam);
				okClicked = true;
				team.setBudget(Integer.parseInt(budgetField.getText()));
				team.setTeamName(nameField.getText());
				team.setShortName(shortNameField.getText());
				team.setHeadCoach(coachComboBox.getValue());
				team.setGround(groundField.getText());
				team.setFounded(foundedDatePicker.getValue());
				team.setStatus(
						statusCheckBox.isSelected() ? ApplicationData.TEAM_ACTIVE : ApplicationData.TEAM_INACTIVE);
				team.setStatusImg(new ImageView(new Image(ApplicationData.TEAM_ACTIVE.equalsIgnoreCase(team.getStatus())
						? "/images/greenC.png" : "/images/redC.png")));
			} catch (TeamDAOException e) {
				AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
			} finally {
				dialogStage.close();
			}
		}

	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Validates the user input in the text fields.
	 *
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		StringBuilder errorMessage = new StringBuilder();

		String budget = budgetField.getText();
		if (StringUtils.isBlank(budget)) {
			errorMessage.append("No valid budget!\n");
		} else if (!StringUtils.isNumeric(budget)) {
			errorMessage.append("No valid budget (must be an integer)!\n");
		} else if (budget.length() > 9)
			errorMessage.append("Budget field accepts only 9 characters!\n");
		else if (Integer.parseInt(budget) < ApplicationData.TEAM_BUDGET_MIN)
			errorMessage.append("Budget should be greater than or equal to " + ApplicationData.TEAM_BUDGET_MIN + "!\n");

		if (StringUtils.isBlank(nameField.getText())) {
			errorMessage.append("No valid name!\n");
		} else if (nameField.getText().length() > 30)
			errorMessage.append("Team name field accepts only 30 characters!\n");

		if (StringUtils.isBlank(shortNameField.getText())) {
			errorMessage.append("No valid short name!\n");
		} else if (shortNameField.getText().length() > 5)
			errorMessage.append("Team short name field accepts only 5 characters!\n");

		if (coachComboBox.getValue() == null || StringUtils.isBlank(coachComboBox.getValue().getCoachCode())
				|| StringUtils.isBlank(coachComboBox.getValue().getCoachName())) {
			errorMessage.append("No valid coach!\n");
		}

		if (StringUtils.isBlank(groundField.getText())) {
			errorMessage.append("No valid ground!\n");
		} else if (groundField.getText().length() > 30)
			errorMessage.append("Ground field accepts only 30 characters!\n");

		LocalDate founded = foundedDatePicker.getValue();
		if (founded == null) {
			errorMessage.append("No valid founded!\n");
		} else if (founded.isAfter(LocalDate.now()) || founded.isEqual(LocalDate.now())) {
			errorMessage.append("Founded date should be before current date!\n");
		}

		if (errorMessage.toString().length() == 0) {
			return true;
		} else {
			AlertDialog.showError("Input Validation", errorMessage.toString());
			return false;
		}
	}
}
