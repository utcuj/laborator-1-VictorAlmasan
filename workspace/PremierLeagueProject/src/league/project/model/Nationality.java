package league.project.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Nationality implements Model{

	private  StringProperty nationalityCode;
	private  StringProperty nationality;

	public Nationality() {
		this(null,null);
	}

	public Nationality(String nationalityCode, String nationality){

		this.nationalityCode = new SimpleStringProperty(nationalityCode);
		this.nationality = new SimpleStringProperty(nationality);
	}

	public String getNationalityCode() {
		return nationalityCode.get();
	}

	public void setNationalityCode(String nationalityCode) {
		this.nationalityCode.set(nationalityCode);
	}

	public StringProperty nationalityCodeProperty() {
		return nationalityCode;
	}

	public String getNationality() {
		return nationality.get();
	}

	public void setNationality(String nationality) {
		this.nationality.set(nationality);
	}

	public StringProperty nationalityProperty() {
		return nationality;
	}

	@Override
	public String toString() {
		return this.getNationality();
	}
}
