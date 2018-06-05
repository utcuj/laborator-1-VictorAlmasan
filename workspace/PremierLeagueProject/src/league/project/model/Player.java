package league.project.model;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Player implements Model{

	private IntegerProperty playerId;
	private StringProperty playerCnp;
	private ObjectProperty<Nationality> nationality;
	private StringProperty firstName;
	private StringProperty lastName;
	private ObjectProperty<LocalDate> dateOfBirth;
	private IntegerProperty height;
	private IntegerProperty weight;
	private ObjectProperty<PlayerPosition> playerPosition;

	public Player() {
		this(0, null, null, null, null, null, 0, 0, null);
	}

	public Player(int playerId,String playerCnp, Nationality nationality, String firstName, String lastName, LocalDate dateOfBirth,
			int height, int weight, PlayerPosition playerPosition) {

		this.playerId = new SimpleIntegerProperty(playerId);
		this.playerCnp = new SimpleStringProperty(playerCnp);
		this.nationality = new SimpleObjectProperty<Nationality>(nationality);
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.dateOfBirth = new SimpleObjectProperty<LocalDate>(dateOfBirth);
		this.height = new SimpleIntegerProperty(height);
		this.weight = new SimpleIntegerProperty(weight);
		this.playerPosition = new SimpleObjectProperty<PlayerPosition>(playerPosition);

	}

	public int getPlayerId() {
		return playerId.get();
	}

	public void setPlayerId(int playerId) {
		this.playerId.set(playerId);
	}

	public IntegerProperty playerIdProperty() {
		return playerId;
	}

	public String getPlayerCnp() {
		return playerCnp.get();
	}

	public void setPlayerCnp(String playerCnp) {
		this.playerCnp.set(playerCnp);
	}

	public StringProperty playerCnpProperty() {
		return playerCnp;
	}

	public Nationality getNationality() {
		return nationality.get();
	}

	public void setNationality(Nationality nationality) {
		this.nationality.set(nationality);
	}

	public ObjectProperty<Nationality> nationalityProperty() {
		return nationality;
	}

	public String getFirstName() {
		return firstName.get();
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	public StringProperty firstNameProperty() {
		return firstName;
	}

	public String getLastName() {
		return lastName.get();
	}

	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	public StringProperty lastNameProperty() {
		return lastName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth.get();
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth.set(dateOfBirth);
	}

	public ObjectProperty<LocalDate> dateOfBirthProperty() {
		return dateOfBirth;
	}

	public int getHeight() {
		return height.get();
	}

	public void setHeight(int height) {
		this.height.set(height);
	}

	public IntegerProperty heightProperty() {
		return height;
	}

	public int getWeight() {
		return weight.get();
	}

	public void setWeight(int weight) {
		this.weight.set(weight);
	}

	public IntegerProperty weightProperty() {
		return weight;
	}

	public PlayerPosition getPlayerPosition() {
		return playerPosition.get();
	}

	public void setPlayerPosition(PlayerPosition playerPosition) {
		this.playerPosition.set(playerPosition);
	}

	public ObjectProperty<PlayerPosition> playerPositionProperty() {
		return playerPosition;
	}

	@Override
	public String toString() {
		return this.getFirstName() + " " + this.getLastName();
	}
}
