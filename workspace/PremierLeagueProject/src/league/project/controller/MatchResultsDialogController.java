package league.project.controller;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import league.project.exception.MatchDAOException;
import league.project.model.Contract;
import league.project.model.Match;
import league.project.model.MatchDAO;
import league.project.model.Player;
import league.project.model.Team;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;
import league.project.util.DateUtil;

public class MatchResultsDialogController {

	private Stage dialogStage;
	private Match match;
	private boolean okClicked = false;
	ObservableList<Contract> scorersData = FXCollections.observableArrayList();

	@FXML
	private Label homeTeamLabel;
	@FXML
	private Label awayTeamLabel;
	@FXML
	private Label matchDateLabel;
	@FXML
	private Label groundLabel;
	@FXML
	private TextField homeTeamField;
	@FXML
	private TextField awayTeamField;
	@FXML
	private TableView<Contract> playersTable;
	@FXML
	private TableColumn<Contract, Player> playerColumn;
	@FXML
	private TableColumn<Contract, Team> teamColumn;
	@FXML
	private TableView<Contract> scorersTable;
	@FXML
	private TableColumn<Contract, Player> scorersNameColumn;
	@FXML
	private TableColumn<Contract, Team> scorersTeamColumn;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		playerColumn.setCellValueFactory(cellData -> cellData.getValue().playerProperty());
		teamColumn.setCellValueFactory(cellData -> cellData.getValue().teamProperty());

		scorersNameColumn.setCellValueFactory(cellData -> cellData.getValue().playerProperty());
		scorersTeamColumn.setCellValueFactory(cellData -> cellData.getValue().teamProperty());

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
	 * Sets the match to be edited in the dialog.
	 *
	 * @param match
	 */
	public void setMatch(Match match) {
		this.match = match;

		homeTeamLabel.setText(match.getHomeTeam() == null ? "" : match.getHomeTeam().getTeamName());
		awayTeamLabel.setText(match.getAwayTeam() == null ? "" : match.getAwayTeam().getTeamName());
		matchDateLabel.setText(match.getMatchDate() == null ? "" : DateUtil.format(match.getMatchDate()));
		groundLabel.setText(match.getHomeTeam() == null ? "" : match.getHomeTeam().getGround());
		try {
			playersTable.setItems(
					MatchDAO.getPlayersList(match.getHomeTeam().getTeamId(), match.getAwayTeam().getTeamId()));
		} catch (MatchDAOException e) {
			AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
		}
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 *
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleAdd() {
		Contract selectedContract = playersTable.getSelectionModel().getSelectedItem();
		scorersData.add(selectedContract);
		scorersTable.setItems(scorersData);
	}

	@FXML
	void handleDelete() {
		Contract selectedContract = scorersTable.getSelectionModel().getSelectedItem();
		scorersData.remove(selectedContract);
		scorersTable.setItems(scorersData);
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Called when the user clicks ok button.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			Optional<ButtonType> result = AlertDialog.showConfirmationDialog("Irreversible Action",
					"Are you sure want to save these results?");
			if (result.get() == ButtonType.OK) {
				try {
					int awayScore = Integer.parseInt(awayTeamField.getText());
					int homeScore = Integer.parseInt(homeTeamField.getText());
					MatchDAO.saveMatchResults(match.getMatchId(), awayScore, homeScore, scorersData);
					match.setNoGoalsAwayTeam(awayScore);
					match.setNoGoalsHomeTeam(homeScore);
					match.setMatchStatusCode(ApplicationData.MATCH_FINISHED);
					match.setStatusImg(new ImageView(new Image("/images/greenC.png")));
					okClicked = true;
					dialogStage.close();
					AlertDialog.showSuccess("Match Results",
							"Results of the match between " + match.getHomeTeam().getTeamName() + " and "
									+ match.getAwayTeam().getTeamName() + " was updated!");

				} catch (MatchDAOException e) {
					AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
				} catch (Exception e) {
					AlertDialog.showError("System Error", "There was an error (" + e.getMessage()
							+ "). Contact the system administrator if this problem persists!");
				}
			}
		}
	}

	private boolean isInputValid() {
		StringBuilder errMsg = new StringBuilder();
		long scorerAwayNo = scorersData.stream()
				.filter(contract -> match.getAwayTeam().getTeamId() == contract.getTeam().getTeamId()).count();
		long scorerHomeNo = scorersData.stream()
				.filter(contract -> match.getHomeTeam().getTeamId() == contract.getTeam().getTeamId()).count();

		if (StringUtils.isBlank(homeTeamField.getText())) {
			errMsg.append("Home team score is mandatory!\n");
		} else if (!StringUtils.isNumeric(homeTeamField.getText())) {
			errMsg.append("No valid home team score (must be an integer)!\n");
		} else if(homeTeamField.getText().length() > 3){
			errMsg.append("Home team score field accepts only 3 characters!\n");
		} else if (Long.parseLong(homeTeamField.getText()) != scorerHomeNo) {
			errMsg.append("Home team score should be equal to home team scorers!\n");
		}

		if (StringUtils.isBlank(awayTeamField.getText())) {
			errMsg.append("Away team score is mandatory!\n");
		} else if (!StringUtils.isNumeric(awayTeamField.getText())) {
			errMsg.append("No valid away team score (must be an integer)!\n");
		} else if(awayTeamField.getText().length() > 3){
			errMsg.append("Away team score field accepts only 3 characters!\n");
		} else if (Long.parseLong(awayTeamField.getText()) != scorerAwayNo) {
			errMsg.append("Away team score should be equal to away team scorers!\n");
		}

		if (errMsg.toString().length() == 0) {
			return true;
		} else {
			AlertDialog.showError("Input Validation", errMsg.toString());
			return false;
		}
	}

}
