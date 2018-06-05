package league.project.controller;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import league.project.exception.ContractDAOException;
import league.project.model.Contract;
import league.project.model.ContractDAO;
import league.project.model.Nationality;
import league.project.model.Player;
import league.project.model.PlayerPosition;
import league.project.model.PlayerRole;
import league.project.model.Team;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;
import league.project.util.CNPValidation;
import league.project.util.DateUtil;

public class ContractAddDialogController {

	private Stage dialogStage;
	private Contract contract;
	private boolean okClicked = false;

	
	@FXML
	private ComboBox<Team> teamComboBox;
	@FXML
	private Label startDateLabel;
	@FXML
	private DatePicker endDatePicker;
	@FXML
	private ComboBox<PlayerRole> playerRoleComboBox;
	@FXML
	private TextField salaryField;
	@FXML
	private ComboBox<Player> playerComboBox;
	@FXML
	private CheckBox addNewPlayerCheckBox;
	@FXML
	private TextField lastNameField;
	@FXML
	private TextField firstNameField;
	@FXML
	private TextField cnpField;
	@FXML
	private ComboBox<Nationality> nationalityComboBox;
	@FXML
	private DatePicker dateOfBirthPicker;
	@FXML
	private ComboBox<PlayerPosition> positionComboBox;
	@FXML
	private TextField heightField;
	@FXML
	private TextField weightField;
	@FXML
	private Label playerPosLabel;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		try {
			teamComboBox.setItems(ContractDAO.getTeamsList());
			playerRoleComboBox.setItems(ContractDAO.getPlayerRolesList());
			playerComboBox.setItems(ContractDAO.getPlayersList());
			nationalityComboBox.setItems(ContractDAO.getNationalitiesList());
			positionComboBox.setItems(ContractDAO.getPlayerPositionsList());
		} catch (ContractDAOException e) {
			AlertDialog.showError("Initializing Add New Contract Form", "Operation failed because : " + e.getMessage());
		}

		playerComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue,
				newValue) -> playerPosLabel.setText("(" + newValue.getPlayerPosition().getPlayerPositionCode() + ")"));
	}

	/**
	 * Sets the team to be edited in the dialog.
	 *
	 * @param team
	 */
	public void setContractForAdd(Contract contract) {
		this.contract = contract;

		teamComboBox.setValue(contract.getTeam());// bun
		startDateLabel.setText(DateUtil.format(LocalDate.now()));// bun
		endDatePicker.setValue(contract.getEndDate());// bun
		playerRoleComboBox.setValue(contract.getPlayerRole());// b
		salaryField.setText(String.valueOf(contract.getSalary()));// b
		playerComboBox.setValue(contract.getPlayer());// b
		addNewPlayerCheckBox.setSelected(false);

	}

	/**
	 * Sets the stage of this dialog.
	 *
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
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
	 * Called when the user clicks save.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			if (addNewPlayerCheckBox.isSelected()) {
				contract.setTeam(teamComboBox.getValue());
				contract.setStartDate(LocalDate.now());
				contract.setEndDate(endDatePicker.getValue());
				contract.setPlayerRole(playerRoleComboBox.getValue());
				contract.setSalary(Integer.parseInt(salaryField.getText()));
				Player player = new Player();
				player.setLastName(lastNameField.getText());
				player.setFirstName(firstNameField.getText());
				player.setPlayerCnp(cnpField.getText());
				player.setNationality(nationalityComboBox.getValue());
				player.setDateOfBirth(dateOfBirthPicker.getValue());
				player.setPlayerPosition(positionComboBox.getValue());
				player.setHeight(Integer.parseInt(heightField.getText()));
				player.setWeight(Integer.parseInt(weightField.getText()));
				contract.setPlayer(player);
			} else {
				contract.setTeam(teamComboBox.getValue());
				contract.setStartDate(LocalDate.now());
				contract.setEndDate(endDatePicker.getValue());
				contract.setPlayerRole(playerRoleComboBox.getValue());
				contract.setSalary(Integer.parseInt(salaryField.getText()));
				contract.setPlayer(playerComboBox.getValue());
			}
			okClicked = true;
			dialogStage.close();
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
		StringBuilder errMsg = new StringBuilder();

		Team team = teamComboBox.getValue();
		if (team == null || StringUtils.isBlank(team.getTeamName()) || StringUtils.isBlank(team.getShortName())
				|| team.getTeamId() == 0) {
			errMsg.append("No valid team!\n");
		}

		LocalDate endDate = endDatePicker.getValue();
		if (endDate == null) {
			errMsg.append("No valid end date!\n");
		} else if (!endDate.isAfter(LocalDate.now().plusDays(ApplicationData.MIN_DAYS_CONTRACT_PERIOD))) {
			errMsg.append("End Date should be greater than or equal to "
					+ DateUtil.format(LocalDate.now().plusDays(ApplicationData.MIN_DAYS_CONTRACT_PERIOD)) + "!\n");
		}

		PlayerRole role = playerRoleComboBox.getValue();
		if (role == null || StringUtils.isBlank(role.getPlayerRoleCode())
				|| StringUtils.isBlank(role.getPlayerRole())) {
			errMsg.append("No valid player role!\n");
		}

		String salary = salaryField.getText();
		if (salary == null)
			errMsg.append("No valid salary!\n");
		else if (!StringUtils.isNumeric(salary))
			errMsg.append("Salary should be integer!\n");
		else if (salary.length() > 9)
			errMsg.append("Salary field accepts only 9 characters!\n");

		if (addNewPlayerCheckBox.isSelected()) {

			if (StringUtils.isBlank(lastNameField.getText())) {
				errMsg.append("No valid last name!\n");
			} else if (lastNameField.getText().length() > 30) {
				errMsg.append("Last name field accepts only 30 characters!\n");
			}

			if (StringUtils.isBlank(firstNameField.getText())) {
				errMsg.append("No valid first name!\n");
			} else if (firstNameField.getText().length() > 30) {
				errMsg.append("First name field accepts only 30 characters!\n");
			}

			LocalDate dateOfBirth = dateOfBirthPicker.getValue();
			if (dateOfBirth == null) {
				errMsg.append("No valid date of birth!\n");
			} else if (!dateOfBirth.isBefore(LocalDate.now().minusYears(ApplicationData.AGE_CONSTRAINTS))) {
				errMsg.append("The player should be at least 16 years old!\n");
			}

			String cnp = cnpField.getText();
			if (StringUtils.isBlank(cnp)) {
				errMsg.append("CNP field is mandatory!\n");
			} else if (!CNPValidation.validate(cnp)) {
				errMsg.append("CNP invalid!\n");
			} else if (!CNPValidation.validateCNPwithBirthdate(cnp, DateUtil.format(dateOfBirth))) {
				errMsg.append("CNP invalid. It should contain the specified birthdate!\n");
			}

			if (nationalityComboBox.getValue() == null
					|| StringUtils.isBlank(nationalityComboBox.getValue().getNationalityCode())
					|| StringUtils.isBlank(nationalityComboBox.getValue().getNationality())) {
				errMsg.append("No valid nationality!\n");
			}

			if (positionComboBox.getValue() == null
					|| StringUtils.isBlank(positionComboBox.getValue().getPlayerPositionCode())
					|| StringUtils.isBlank(positionComboBox.getValue().getPlayerPosition())) {
				errMsg.append("No valid player position!\n");
			}

			if (StringUtils.isNotBlank(heightField.getText())) {
				if (heightField.getText().length() > 3)
					errMsg.append("Height field accepts only 3 characters!\n");
				else if (!StringUtils.isNumeric(heightField.getText()))
					errMsg.append("No valid height (must be an integer)!\n");
			}

			if (StringUtils.isNotBlank(weightField.getText())) {
				if (weightField.getText().length() > 3)
					errMsg.append("Weight field accepts only 3 characters!\n");
				else if (!StringUtils.isNumeric(weightField.getText()))
					errMsg.append("No valid Weight (must be an integer)!\n");
			}

		} else {
			if (playerComboBox.getValue() == null || StringUtils.isBlank(playerComboBox.getValue().getFirstName())
					|| StringUtils.isBlank(playerComboBox.getValue().getLastName())
					|| playerComboBox.getValue().getPlayerId() == 0
					|| StringUtils.isBlank(playerComboBox.getValue().getPlayerCnp())) {
				errMsg.append("No valid player!\n");
			}
		}

		if (errMsg.toString().length() == 0) {
			return true;
		} else {
			AlertDialog.showError("Input Validation", errMsg.toString());
			return false;
		}

	}

}
