package league.project.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import league.project.MainApp;
import league.project.exception.ContractDAOException;
import league.project.exception.DuplicateEntryException;
import league.project.model.Contract;
import league.project.model.ContractDAO;
import league.project.model.Player;
import league.project.model.Team;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;
import league.project.util.DateUtil;

public class ContractsOverviewController {

	@FXML
	private TableView<Contract> contractsTable;
	@FXML
	private TableColumn<Contract, Player> playerNameColumn;
	@FXML
	private TableColumn<Contract, Team> teamNameColumn;
	@FXML
	private TableColumn<Contract, LocalDate> endDateColumn;
	@FXML
	private TableColumn<Contract, ImageView> statusColumn;

	@FXML
	private Label idContractLabel;
	@FXML
	private Label playerCnpLabel;
	@FXML
	private Label playerNameLabel;
	@FXML
	private Label teamNameLabel;
	@FXML
	private Label playerRoleLabel;
	@FXML
	private Label startDateLabel;
	@FXML
	private Label endDateLabel;
	@FXML
	private Label salaryLabel;

	@FXML
	private SplitPane verticalSplitPane;

	@FXML
	private TextField teamNameField;
	@FXML
	private TextField playerNameField;

	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public ContractsOverviewController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		// Initialize the contractsTable with the four columns.
		playerNameColumn.setCellValueFactory(cellData -> cellData.getValue().playerProperty());
		teamNameColumn.setCellValueFactory(cellData -> cellData.getValue().teamProperty());
		endDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
		statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusImgProperty());

		// Clear contract details.
		showContractInformation(null);

		// Clear contract search fields
		setSearchFields();

		// Listen for selection changes in table and show the contract details
		// when
		// changed.
		contractsTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showContractInformation(newValue));
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	private void setSearchFields() {
		teamNameField.setText("");
		playerNameField.setText("");
	}

	/**
	 * Fills all text fields to show information about the contract. If the
	 * specified contract is null, all labels are cleared.
	 *
	 * @param contract
	 *            the contract or null
	 */
	private void showContractInformation(Contract contract) {
		if (contract != null) {
			// Fill the labels with info from the contract object.
			idContractLabel.setText(String.valueOf(contract.getContractId()));
			playerCnpLabel.setText(contract.getPlayer() == null ? null : contract.getPlayer().getPlayerCnp());
			playerNameLabel.setText(contract.getPlayer() == null ? ""
					: contract.getPlayer().getFirstName() + " " + contract.getPlayer() == null ? ""
							: contract.getPlayer().getLastName());
			teamNameLabel.setText(contract.getTeam() == null ? null : contract.getTeam().getTeamName());
			playerRoleLabel.setText(contract.getPlayerRole() == null ? null : contract.getPlayerRole().getPlayerRole());
			startDateLabel.setText(DateUtil.format(contract.getStartDate()));
			endDateLabel.setText(DateUtil.format(contract.getEndDate()));
			salaryLabel.setText(String.valueOf(contract.getSalary()));
		} else {
			// contract is null, remove all texts.
			idContractLabel.setText("");
			playerCnpLabel.setText("");
			playerNameLabel.setText("");
			teamNameLabel.setText("");
			playerRoleLabel.setText("");
			startDateLabel.setText("");
			endDateLabel.setText("");
			salaryLabel.setText("");
		}
	}

	/**
	 * Called when the user clicks the new button. Opens a dialog to edit
	 * details for a new team.
	 */
	@FXML
	private void handleNewContract() {
		Contract contract = new Contract(0, null, null, ApplicationData.CONTRACT_ACTIVE, null, null, null, 0, null);
		boolean okClicked = showContractAddDialog(contract, "New Contract");
		if (okClicked) {
			try {
				int newId = ContractDAO.saveContract(contract);
				if (newId > 0) {
					contract.setContractId(newId);
					contract.setStatusImg(new ImageView(new Image("/images/greenC.png")));
					contractsTable.getItems().add(contract);
					AlertDialog.showSuccess("New Contract",
							"Contract between " + contract.getTeam().getTeamName() + " team and "
									+ contract.getPlayer().getFirstName() + " " + contract.getPlayer().getLastName()
									+ " player was saved!");
				}
			} catch (DuplicateEntryException e) {
				AlertDialog.showError("Duplicate Contract",
						"A player can have only a contract! Note, if a new player is added and there is a player having the same cnp (with or without contract), the contract cannot be saved!");
			} catch (ContractDAOException e) {
				AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
			} catch (Exception e) {
				AlertDialog.showError("System Error", "There was an error (" + e.getMessage()
						+ "). Contact the system administrator if this problem persists!");
			}
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected contract.
	 */
	@FXML
	private void handleEditContract() {
		Contract selectedContract = contractsTable.getSelectionModel().getSelectedItem();
		if (selectedContract != null) {
			if (ApplicationData.CONTRACT_ACTIVE.equalsIgnoreCase(selectedContract.getStatusCode())) {
				boolean okClicked = showContractEditDialog(selectedContract, "Renew Contract");
				if (okClicked) {
					showContractInformation(selectedContract);
				}
			} else {
				AlertDialog.showInformation("Renew Contract", "Only active contracts can be renewed!");
			}

		} else {
			AlertDialog.showError("Input Validation", "Select a contract!");
		}
	}

	/**
	 * Called when the user clicks the Search button.
	 */
	@FXML
	private void handleSearchContract() {

		try {
			contractsTable.setItems(ContractDAO.getContractList(teamNameField.getText(), playerNameField.getText()));
			// no row selected
			if (contractsTable.getItems().isEmpty()) {
				AlertDialog.showInformation("Search Contract", "Your search yielded no results!");
			}

		} catch (ContractDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
		}
	}

	@FXML
	private void handleReset() {
		showContractInformation(null);
		contractsTable.getItems().clear();
		teamNameField.setText("");
		playerNameField.setText("");
	}

	/**
	 * Delete button.
	 */
	@FXML
	private void handleDeleteContract() {
		try {
			int selectedIndex = contractsTable.getSelectionModel().getSelectedIndex();
			if (selectedIndex != -1) {
				Contract selectedContract = contractsTable.getSelectionModel().getSelectedItem();
				if (ApplicationData.CONTRACT_ACTIVE.equalsIgnoreCase(selectedContract.getStatusCode())) {
					Optional<ButtonType> result = AlertDialog.showConfirmationDialog("Irreversible Action",
							"Are you sure you want to delete contract between " + selectedContract.getTeam().getTeamName() + " and " + selectedContract.getPlayer().getFirstName() + " "+selectedContract.getPlayer().getLastName() + " ?");
					if (result.get() == ButtonType.OK) {
						if (ContractDAO.deleteContract(selectedContract.getContractId())) {
							selectedContract.setStatusCode(ApplicationData.CONTRACT_RENEWED);
							selectedContract.setStatusImg(new ImageView(new Image("/images/orangeC.png")));
							AlertDialog.showSuccess("Delete Contract",
									"Contract  between " + selectedContract.getTeam().getTeamName() + " and " + selectedContract.getPlayer().getFirstName() + " "+selectedContract.getPlayer().getLastName() + " was cancelled.");
						} else {
							AlertDialog.showInformation("Delete Contract",
									"There is no contract  between " + selectedContract.getTeam().getTeamName() + " and " + selectedContract.getPlayer().getFirstName() + " "+selectedContract.getPlayer().getLastName() + ".");
						}
					}
				} else {
					AlertDialog.showInformation("Delete Contract", "Only active contracts can be cancelled!");
				}
			} else {
				AlertDialog.showError("Input Validation", "Select a contract!");
			}
		} catch (ContractDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
		} catch (Exception e) {
			AlertDialog.showError("System Error", "There was an error (" + e.getMessage()
					+ "). Contact the system administrator if this problem persists!");
		}
	}

	/**
	 * Opens a dialog to edit details for the specified contract. If the user
	 * clicks OK, the changes are saved into the provided contract object and
	 * true is returned.
	 *
	 * @param contract
	 *            the contract object to be added
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showContractAddDialog(Contract contract, String headerDialog) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ContractAddDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle(headerDialog);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setResizable(false);
			dialogStage.setHeight(380);
			dialogStage.setWidth(700);

			// Set the contract into the controller.
			ContractAddDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setContractForAdd(contract);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			AlertDialog.showError("System Error", "There was an error (" + e.getMessage()
					+ "). Contact the system administrator if this problem persists!");
			return false;
		}
	}

	/**
	 * Opens a dialog to edit details for the specified contract. If the user
	 * clicks OK, the changes are saved into the provided contract object and
	 * true is returned.
	 *
	 * @param contract
	 *            the contract object to be added
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showContractEditDialog(Contract contract, String headerDialog) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ContractEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle(headerDialog);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainApp.getPrimaryStage());
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.setResizable(false);
			dialogStage.setHeight(300);
			dialogStage.setWidth(676);

			// Set the contract into the controller.
			ContractEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setContractForEdit(contract);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			AlertDialog.showError("System Error", "There was an error (" + e.getMessage()
					+ "). Contact the system administrator if this problem persists!");
			return false;
		}
	}

}
