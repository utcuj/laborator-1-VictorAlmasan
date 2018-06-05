package league.project.model;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;
import league.project.util.ApplicationData;

public class Match implements Model{

	private IntegerProperty matchId;
	private ObjectProperty<Team> homeTeam;
	private ObjectProperty<Team> awayTeam;
	private ObjectProperty<MatchRound> round;
	private ObjectProperty<LocalDate> matchDate;
	private IntegerProperty noGoalsHomeTeam;
	private IntegerProperty noGoalsAwayTeam;
	private StringProperty matchStatusCode;
	private ObjectProperty<ImageView> statusImg;

	public Match() {
		this(0, null, null, null, 0, 0, null,null, null);
	}

	public Match(int matchId, Team homeTeam, Team awayTeam, LocalDate matchDate, int noGoalsHomeTeam,
			int noGoalsAwayTeam, String matchStatusCode, ImageView statusImg, MatchRound round) {

		this.matchId = new SimpleIntegerProperty(matchId);
		this.homeTeam = new SimpleObjectProperty<Team>(homeTeam);
		this.awayTeam = new SimpleObjectProperty<Team>(awayTeam);
		this.matchDate = new SimpleObjectProperty<LocalDate>(matchDate);
		this.noGoalsHomeTeam = new SimpleIntegerProperty(noGoalsHomeTeam);
		this.noGoalsAwayTeam = new SimpleIntegerProperty(noGoalsAwayTeam);
		this.matchStatusCode = new SimpleStringProperty(matchStatusCode);
		this.statusImg = new SimpleObjectProperty<ImageView>(statusImg);
		this.round = new SimpleObjectProperty<MatchRound>(round);
	}

	public int getMatchId() {
		return matchId.get();
	}

	public void setMatchId(int matchId) {
		this.matchId.set(matchId);
	}

	public IntegerProperty matchIdProperty() {
		return matchId;
	}

	public Team getHomeTeam() {
		return homeTeam.get();
	}

	public void setHomeTeam(Team homeTeam) {
		this.homeTeam.set(homeTeam);
	}

	public ObjectProperty<Team> homeTeamProperty() {
		return homeTeam;
	}

	public Team getAwayTeam() {
		return awayTeam.get();
	}

	public void setAwayTeam(Team awayTeam) {
		this.awayTeam.set(awayTeam);
	}

	public ObjectProperty<Team> awayTeamProperty() {
		return awayTeam;
	}

	public MatchRound getRound() {
		return round.get();
	}

	public void setRound(MatchRound round) {
		this.round.set(round);
	}

	public ObjectProperty<MatchRound> roundProperty() {
		return round;
	}

	public LocalDate getMatchDate() {
		return matchDate.get();
	}

	public void setMatchDate(LocalDate matchDate) {
		this.matchDate.set(matchDate);
	}

	public ObjectProperty<LocalDate> matchDateProperty() {
		return matchDate;
	}

	public int getNoGoalsHomeTeam() {
		return noGoalsHomeTeam.get();
	}

	public void setNoGoalsHomeTeam(int noGoalsHomeTeam) {
		this.noGoalsHomeTeam.set(noGoalsHomeTeam);
	}

	public IntegerProperty noGoalsHomeTeamProperty() {
		return noGoalsHomeTeam;
	}

	public int getNoGoalsAwayTeam() {
		return noGoalsAwayTeam.get();
	}

	public void setNoGoalsAwayTeam(int noGoalsAwayTeam) {
		this.noGoalsAwayTeam.set(noGoalsAwayTeam);
	}

	public IntegerProperty noGoalsAwayTeamProperty() {
		return noGoalsAwayTeam;
	}

	public String getMatchStatusCode() {
		return matchStatusCode.get();
	}

	public void setMatchStatusCode(String matchStatusCode) {
		this.matchStatusCode.set(matchStatusCode);
	}

	public StringProperty matchStatusCodeProperty() {
		return matchStatusCode;
	}

	public void setStatusImg(ImageView statusImg) {
		this.statusImg.set(statusImg);
	}

	public ObjectProperty<ImageView> statusImgProperty() {
		return statusImg;
	}

	public String getImageByStatus() {
		return ApplicationData.MATCH_FINISHED.equalsIgnoreCase(getMatchStatusCode()) ? "/images/greenC.png" : ApplicationData.MATCH_BANKRUPTCY.equalsIgnoreCase(getMatchStatusCode()) ? "/images/redC.png" : ApplicationData.MATCH_PENDING.equalsIgnoreCase(getMatchStatusCode()) ? "/images/blueC.png" : "/images/orangeC.png";
	}

}
