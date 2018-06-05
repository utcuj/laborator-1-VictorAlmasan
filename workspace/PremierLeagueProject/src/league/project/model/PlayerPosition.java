package league.project.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlayerPosition implements Model{

	private  StringProperty playerPositionCode;
	private  StringProperty playerPosition;

	public PlayerPosition() {
		this(null,null);
	}

	public PlayerPosition(String playerPositionCode, String playerPosition){

		this.playerPositionCode = new SimpleStringProperty(playerPositionCode);
		this.playerPosition = new SimpleStringProperty(playerPosition);
	}

	public String getPlayerPositionCode() {
		return playerPositionCode.get();
	}

	public void setPlayerPositionCode(String playerPositionCode) {
		this.playerPositionCode.set(playerPositionCode);
	}

	public StringProperty playerPositionCodeProperty() {
		return playerPositionCode;
	}

	public String getPlayerPosition() {
		return playerPosition.get();
	}

	public void setPlayerPosition(String playerPosition) {
		this.playerPosition.set(playerPosition);
	}

	public StringProperty playerPositionProperty() {
		return playerPosition;
	}

	@Override
	public String toString() {
		return this.getPlayerPosition();
	}
}
