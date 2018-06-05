package league.project.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Coach implements Model{

	private  StringProperty coachCode;
	private  StringProperty coachName;

	public Coach() {
		this(null,null);
	}

	public Coach(String coachCode, String coachName){

		this.coachCode = new SimpleStringProperty(coachCode);
		this.coachName = new SimpleStringProperty(coachName);
	}

	public String getCoachCode() {
		return coachCode.get();
	}

	public void setCoachCode(String coachCode) {
		this.coachCode.set(coachCode);
	}

	public StringProperty coachCodeProperty() {
		return coachCode;
	}

	public String getCoachName() {
		return coachName.get();
	}

	public void setCoachName(String coachName) {
		this.coachName.set(coachName);
	}

	public StringProperty coachNameProperty() {
		return coachName;
	}

	@Override
	public String toString() {
		return this.getCoachName();
	}


}
