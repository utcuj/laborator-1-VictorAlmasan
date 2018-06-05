package league.project.model;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Application {

	private StringProperty code;
	private IntegerProperty status;
	private ObjectProperty<LocalDate> startDate;

	public Application() {
		this(null, 0, null);
	}

	public Application(String code, int status, LocalDate startDate) {
		this.status = new SimpleIntegerProperty(status);
		this.code = new SimpleStringProperty(code);
		this.startDate = new SimpleObjectProperty<LocalDate>(startDate);
	}

	public int getStatus() {
		return status.get();
	}

	public void setStatus(int status) {
		this.status.set(status);
	}

	public IntegerProperty statusProperty() {
		return status;
	}

	public String getCode() {
		return code.get();
	}

	public void setCode(String code) {
		this.code.set(code);
	}

	public StringProperty codeProperty() {
		return code;
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

}
