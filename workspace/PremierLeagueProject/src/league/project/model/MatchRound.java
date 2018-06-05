package league.project.model;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MatchRound implements Model{

	private IntegerProperty code;
	private StringProperty name;
	private ObjectProperty<LocalDate> startDate;
	private ObjectProperty<LocalDate> endDate;

	public MatchRound(){
		this(0,null,null,null);
	}

	public MatchRound(int code, String name, LocalDate startDate, LocalDate endDate){
		this.code=new SimpleIntegerProperty(code);
		this.name=new SimpleStringProperty(name);
		this.startDate= new SimpleObjectProperty<LocalDate>(startDate);
		this.endDate= new SimpleObjectProperty<LocalDate>(endDate);
	}

	public int getCode() {
		return code.get();
	}

	public void setCode(int code) {
		this.code.set(code);
	}

	public IntegerProperty codeProperty() {
		return code;
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty groundName() {
		return name;
	}

	public LocalDate getStartDate() {
		return startDate.get();
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate.set(startDate);
	}

	public ObjectProperty<LocalDate> startDateProperty() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate.get();
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate.set(endDate);
	}

	public ObjectProperty<LocalDate> endDateProperty() {
		return endDate;
	}

	@Override
	public String toString() {
		return getName();
	}

}
