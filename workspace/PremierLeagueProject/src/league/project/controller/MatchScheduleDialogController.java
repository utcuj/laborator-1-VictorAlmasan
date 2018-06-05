package league.project.controller;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import league.project.exception.MatchDAOException;
import league.project.model.Match;
import league.project.model.MatchDAO;
import league.project.model.MatchRound;
import league.project.util.AlertDialog;
import league.project.util.ApplicationData;

public class MatchScheduleDialogController {

	private Stage dialogStage;
	private Match match;
	private boolean okClicked = false;

	@FXML
	private Label homeTeamLabel;
	@FXML
	private Label awayTeamLabel;
	@FXML
	private ComboBox<MatchRound> roundCombo;
	@FXML
	private ComboBox<LocalDate> dateCombo;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		roundCombo.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> populateDateCombo(newValue));

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
	public void setMatchSchedule(Match match) {
		this.match = match;

		homeTeamLabel.setText(match.getHomeTeam() == null ? "" : match.getHomeTeam().getTeamName());
		awayTeamLabel.setText(match.getAwayTeam() == null ? "" : match.getAwayTeam().getTeamName());

		try {
			if(ApplicationData.MATCH_PENDING.equals(match.getMatchStatusCode())){
				ObservableList<MatchRound> matchRound = FXCollections.observableArrayList();
				matchRound.add(match.getRound());
				roundCombo.setItems(matchRound);
			}else
			roundCombo.setItems(
					MatchDAO.getMatchRoundList(match.getHomeTeam().getTeamId(), match.getAwayTeam().getTeamId()));
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

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Called when the user clicks Ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			try {
				MatchDAO.updateMatch(match.getMatchId(), roundCombo.getValue().getCode(), dateCombo.getValue());
				match.setMatchDate(dateCombo.getValue());
				match.setRound(roundCombo.getValue());
				match.setMatchStatusCode(ApplicationData.MATCH_PENDING);
				match.setStatusImg(new ImageView(new Image("/images/blueC.png")));
				okClicked = true;
				dialogStage.close();
				AlertDialog.showSuccess("Success", "Match between " + match.getHomeTeam().getTeamName() + " and "
						+ match.getAwayTeam().getTeamName() + " was scheduled!");

			} catch (MatchDAOException e) {
				AlertDialog.showError("Internal Error", "Operation failed because : " + e.getMessage());
			} catch (Exception e) {
				AlertDialog.showError("System Error", "There was an error (" + e.getMessage()
						+ "). Contact the system administrator if this problem persists!");
			}
		}
	}

	private boolean isInputValid() {
		StringBuilder errMsg = new StringBuilder();

		LocalDate date = dateCombo.getValue();
		if (date == null) {
			errMsg.append("No valid match date!\n");
		}

		MatchRound round = roundCombo.getValue();
		if (round == null || round.getCode() == 0)
			errMsg.append("No valid match round!\n");

		if (errMsg.toString().length() == 0) {
			return true;
		} else {
			AlertDialog.showError("Input Validation", errMsg.toString());
			return false;
		}
	}

	private void populateDateCombo(MatchRound round) {
		dateCombo.getItems().clear();
		if (round != null && round.getStartDate() != null) {
			LocalDate date = round.getStartDate();
			dateCombo.getItems().add(date);
			for (int i = 0; i < ApplicationData.ROUND_DAYS; i++) {
				date = date.plusDays(1);
				dateCombo.getItems().add(date);
			}
		} else {
			AlertDialog.showError("Internal Error", "Operation failed when trying to populate date field!");
		}
	}
}
