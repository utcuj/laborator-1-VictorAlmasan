package league.project.controller;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import league.project.exception.ContractDAOException;
import league.project.model.Contract;
import league.project.model.ContractDAO;
import league.project.model.Player;
import league.project.model.PlayerRole;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;
import league.project.util.DateUtil;

public class ContractEditDialogController {

	private Stage dialogStage;
	private Contract contract;
	private boolean okClicked = false;

	@FXML
	private Label teamNameLabel;
	@FXML
	private Label startDateLabel;
	@FXML
	private DatePicker endDatePicker;
	@FXML
	private Label lastNameLabel;
	@FXML
	private Label firstNameLabel;
	@FXML
	private Label cnpLabel;
	@FXML
	private Label nationalityLabel;
	@FXML
	private Label dateOfBirthLabel;
	@FXML
	private Label postionLabel;
	@FXML
	private Label heightLabel;
	@FXML
	private Label weightLabel;
	@FXML
	private ComboBox<PlayerRole> playerRoleComboBox;
	@FXML
	private TextField salaryField;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		try {
			playerRoleComboBox.setItems(ContractDAO.getPlayerRolesList());
		} catch (ContractDAOException e) {
			AlertDialog.showError("Populate Player Role List", "Operation failed because : " + e.getMessage());
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

	/**
	 * Sets the contract to be edited in the dialog.
	 *
	 * @param contract
	 */
	public void setContractForEdit(Contract contract) {
		this.contract = contract;

		Player player = contract.getPlayer();
		teamNameLabel.setText(contract.getTeam() == null ? null : contract.getTeam().getTeamName());// bun
		startDateLabel.setText(DateUtil.format(LocalDate.now()));// bun
		endDatePicker.setValue(contract.getEndDate());// bun
		lastNameLabel.setText(player == null ? null : player.getLastName());// b
		firstNameLabel.setText(player == null ? null : player.getFirstName());// b
		cnpLabel.setText(player == null ? null : player.getPlayerCnp());// b
		nationalityLabel.setText(player.getNationality() == null ? null : player.getNationality().getNationality());
		dateOfBirthLabel.setText(player.getDateOfBirth() == null ? null : DateUtil.format(player.getDateOfBirth()));// b
		postionLabel
				.setText(player.getPlayerPosition() == null ? null : player.getPlayerPosition().getPlayerPosition());// b
		heightLabel.setText(player == null ? null : String.valueOf(player.getHeight()));
		weightLabel.setText(player == null ? null : String.valueOf(player.getWeight()));
		playerRoleComboBox.setValue(contract.getPlayerRole());
		salaryField.setText(String.valueOf(contract.getSalary()));
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
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			try {
				ContractDAO.renewContract(contract.getContractId(), LocalDate.now(), endDatePicker.getValue(),
						playerRoleComboBox.getValue(), Integer.parseInt(salaryField.getText()));
				contract.setStartDate(LocalDate.now());
				contract.setEndDate(endDatePicker.getValue());
				contract.setPlayerRole(playerRoleComboBox.getValue());
				contract.setSalary(Integer.parseInt(salaryField.getText()));
				okClicked = true;
				dialogStage.close();
				AlertDialog.showSuccess("Success",
						"Contract between " + contract.getTeam().getTeamName() + " team and "
								+ contract.getPlayer().getFirstName() + " " + contract.getPlayer().getLastName()
								+ " player was renewed!");
			} catch (ContractDAOException e) {
				AlertDialog.showError("Renew Contract", "Operation failed because: " + e.getMessage());
			}
		}
	}

	/**
	 * Form validation.
	 *
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		StringBuilder errMsg = new StringBuilder();

		LocalDate endDate = endDatePicker.getValue();
		if (endDate == null) {
			errMsg.append("Select End Date!\n");
		} else if (!endDate.isAfter(LocalDate.now().plusDays(ApplicationData.MIN_DAYS_CONTRACT_PERIOD))) {
			errMsg.append("End Date should be greater than or equal to "
					+ DateUtil.format(LocalDate.now().plusDays(ApplicationData.MIN_DAYS_CONTRACT_PERIOD)) + "!\n");
		}

		String salary = salaryField.getText();
		if (salary == null)
			errMsg.append("No valid salary!\n");
		else if (!StringUtils.isNumeric(salary))
			errMsg.append("Salary should be integer!\n");
		else if (salary.length() > 9)
			errMsg.append("Salary field accepts only 9 characters!\n");


		PlayerRole playerRole = playerRoleComboBox.getValue();
		if (playerRole == null || StringUtils.isBlank(playerRole.getPlayerRole())
				|| StringUtils.isBlank(playerRole.getPlayerRoleCode())) {
			errMsg.append("Select Player Role!\n");
		}

		if (errMsg.toString().length() == 0) {
			return true;
		} else {
			AlertDialog.showError("Input Validation", errMsg.toString());
			return false;
		}
	}
}
