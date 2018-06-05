package league.project.model;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlayerRole implements Serializable, Model{

	private static final long serialVersionUID = 3252581549904742537L;

	private StringProperty playerRoleCode;
	private StringProperty playerRole;

	public PlayerRole() {
		this(null, null);
	}

	public PlayerRole(String playerRoleCode, String playerRole) {

		this.playerRoleCode = new SimpleStringProperty(playerRoleCode);
		this.playerRole = new SimpleStringProperty(playerRole);
	}

	public String getPlayerRoleCode() {
		return playerRoleCode.get();
	}

	public void setPlayerRoleCode(String playerRoleCode) {
		this.playerRoleCode.set(playerRoleCode);
	}

	public StringProperty playerRoleCodeProperty() {
		return playerRoleCode;
	}

	public String getPlayerRole() {
		return playerRole.get();
	}

	public void setPlayerRole(String playerRole) {
		this.playerRole.set(playerRole);
	}

	public StringProperty playerRoleProperty() {
		return playerRole;
	}

	@Override
	public String toString() {
		return this.getPlayerRole();
	}
}
