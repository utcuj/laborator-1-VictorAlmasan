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

public class Contract implements Model{

	private IntegerProperty contractId;
	private ObjectProperty<Player> player;
	private ObjectProperty<Team> team;
	private StringProperty statusCode;
	private ObjectProperty<PlayerRole> playerRole;
	private ObjectProperty<LocalDate> startDate;
	private ObjectProperty<LocalDate> endDate;
	private IntegerProperty salary;
	private ObjectProperty<ImageView> statusImg;

	public Contract() {
		this(0, null, null, null, null, null, null, 0, null);
	}

	public Contract(int contractId, Player player, Team team, String statusCode, PlayerRole playerRole,
			LocalDate startDate, LocalDate endDate, int salary, ImageView statusImg) {

		this.contractId = new SimpleIntegerProperty(contractId);
		this.player = new SimpleObjectProperty<Player>(player);
		this.team = new SimpleObjectProperty<Team>(team);
		this.statusCode = new SimpleStringProperty(statusCode);
		this.playerRole = new SimpleObjectProperty<PlayerRole>(playerRole);
		this.startDate = new SimpleObjectProperty<LocalDate>(startDate);
		this.endDate = new SimpleObjectProperty<LocalDate>(endDate);
		this.salary = new SimpleIntegerProperty(salary);
		this.statusImg = new SimpleObjectProperty<ImageView>(statusImg);
	}

	public Contract(Contract newContract) {
		this.contractId = newContract.contractId;
		Nationality nationality = new Nationality(newContract.getPlayer().getNationality().getNationalityCode(), newContract.getPlayer().getNationality().getNationality());
		PlayerPosition playerPos = new PlayerPosition(newContract.getPlayer().getPlayerPosition().getPlayerPositionCode(), newContract.getPlayer().getPlayerPosition().getPlayerPosition());
		Player player = new Player(newContract.getPlayer().getPlayerId(), newContract.getPlayer().getPlayerCnp(), nationality, newContract.getPlayer().getFirstName(), newContract.getPlayer().getLastName(), newContract.getPlayer().getDateOfBirth(), newContract.getPlayer().getHeight(), newContract.getPlayer().getWeight(), playerPos);
		this.player= new SimpleObjectProperty<Player>(player);
		Coach coach = new Coach(newContract.getTeam().getHeadCoach().getCoachCode(), newContract.getTeam().getHeadCoach().getCoachName());
		Team team = new Team(newContract.getTeam().getTeamId(), newContract.getTeam().getBudget(), newContract.getTeam().getTeamName(), newContract.getTeam().getShortName(), coach, newContract.getTeam().getGround(), newContract.getTeam().getFounded(), newContract.getTeam().getStatus(), null);
		this.team= new SimpleObjectProperty<Team>(team);
		this.statusCode=newContract.statusCode;
		PlayerRole playerRole = new PlayerRole(newContract.getPlayerRole().getPlayerRoleCode(), newContract.getPlayerRole().getPlayerRole());
		this.playerRole=new SimpleObjectProperty<PlayerRole>(playerRole);
		this.startDate=newContract.startDate;
		this.endDate=newContract.endDate;
		this.salary=newContract.salary;
	}

	public int getContractId() {
		return contractId.get();
	}

	public void setContractId(int contractId) {
		this.contractId.set(contractId);
	}

	public IntegerProperty contractIdProperty() {
		return contractId;
	}

	public Player getPlayer() {
		return player.get();
	}

	public void setPlayer(Player player) {
		this.player.set(player);
	}

	public ObjectProperty<Player> playerProperty() {
		return player;
	}

	public Team getTeam() {
		return team.get();
	}

	public void setTeam(Team team) {
		this.team.set(team);
	}

	public ObjectProperty<Team> teamProperty() {
		return team;
	}

	public String getStatusCode() {
		return statusCode.get();
	}

	public void setStatusCode(String statusCode) {
		this.statusCode.set(statusCode);
	}

	public StringProperty statusCodeProperty() {
		return statusCode;
	}

	public PlayerRole getPlayerRole() {
		return playerRole.get();
	}

	public void setPlayerRole(PlayerRole playerRole) {
		this.playerRole.set(playerRole);
	}

	public ObjectProperty<PlayerRole> playerRoleProperty() {
		return playerRole;
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

	public int getSalary() {
		return salary.get();
	}

	public void setSalary(int salary) {
		this.salary.set(salary);
	}

	public IntegerProperty salaryProperty() {
		return salary;
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
		return ApplicationData.CONTRACT_ACTIVE.equalsIgnoreCase(getStatusCode()) ? "/images/greenC.png"
				: ApplicationData.CONTRACT_BANKRUPTCY.equalsIgnoreCase(getStatusCode()) ? "/images/redC.png"
						: "/images/orangeC.png";
	}

}
