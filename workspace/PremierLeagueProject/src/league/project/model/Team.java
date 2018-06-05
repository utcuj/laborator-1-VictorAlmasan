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

public class Team implements Model{

	private IntegerProperty teamId;
	private IntegerProperty budget;
	private StringProperty teamName;
	private StringProperty shortName;
	private ObjectProperty<Coach> headCoach;
	private StringProperty ground;
	private ObjectProperty<LocalDate> founded;
	private StringProperty status;
	private ObjectProperty<ImageView> statusImg;

	public Team() {

		this(0, ApplicationData.TEAM_BUDGET_MIN, null, null, null, null, null, null,null);

	}

	public Team(int teamId, int budget, String teamName, String shortName, Coach headCoach, String ground,
			LocalDate founded, String status, ImageView statusImg) {

		this.teamId = new SimpleIntegerProperty(teamId);
		this.budget = new SimpleIntegerProperty(budget);
		this.teamName = new SimpleStringProperty(teamName);
		this.shortName = new SimpleStringProperty(shortName);
		this.headCoach = new SimpleObjectProperty<Coach>(headCoach);
		this.ground = new SimpleStringProperty(ground);
		this.founded = new SimpleObjectProperty<LocalDate>(founded);
		this.status = new SimpleStringProperty(status);
		this.statusImg = new SimpleObjectProperty<ImageView>(statusImg);

	}

	public int getTeamId() {
		return teamId.get();
	}

	public void setTeamId(int teamId) {
		this.teamId.set(teamId);
	}

	public IntegerProperty teamIdProperty() {
		return teamId;
	}

	public int getBudget() {
		return budget.get();
	}

	public void setBudget(int budget) {
		this.budget.set(budget);
	}

	public IntegerProperty budgetProperty() {
		return budget;
	}

	public String getTeamName() {
		return teamName.get();
	}

	public void setTeamName(String teamName) {
		this.teamName.set(teamName);
	}

	public StringProperty teamNameProperty() {
		return teamName;
	}

	public String getShortName() {
		return shortName.get();
	}

	public void setShortName(String shortName) {
		this.shortName.set(shortName);
	}

	public StringProperty shortNameProperty() {
		return shortName;
	}

	public Coach getHeadCoach() {
		return headCoach.get();
	}

	public void setHeadCoach(Coach headCoach) {
		this.headCoach.set(headCoach);
	}

	public ObjectProperty<Coach> headCoachProperty() {
		return headCoach;
	}

	public String getGround() {
		return ground.get();
	}

	public void setGround(String ground) {
		this.ground.set(ground);
	}

	public StringProperty groundProperty() {
		return ground;
	}

	public LocalDate getFounded() {
		return founded.get();
	}

	public void setFounded(LocalDate founded) {
		this.founded.set(founded);
	}

	public ObjectProperty<LocalDate> foundedProperty() {
		return founded;
	}

	public String getStatus() {
		return status.get();
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	public StringProperty statusProperty() {
		return status;
	}

	public ImageView getStatusImg() {
		return statusImg.get();
	}

	public void setStatusImg(ImageView statusImg) {
		this.statusImg.set(statusImg);
	}

	public ObjectProperty<ImageView> statusImgProperty() {
		return statusImg;
	}

	public String getImageByStatus() {
		return ApplicationData.TEAM_ACTIVE.equalsIgnoreCase(getStatus()) ? "/images/greenC.png" : "/images/redC.png";
	}



	@Override
	public String toString() {
		return getTeamName();
	}

}
