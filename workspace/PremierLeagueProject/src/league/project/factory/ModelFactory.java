package league.project.factory;

import league.project.model.Coach;
import league.project.model.Contract;
import league.project.model.Match;
import league.project.model.MatchRound;
import league.project.model.Model;
import league.project.model.Nationality;
import league.project.model.Player;
import league.project.model.PlayerPosition;
import league.project.model.PlayerRole;
import league.project.model.Team;

public class ModelFactory {
	
	public Model getModel(String type) {
		if (type == null) {
			return null;
		}
		if (type.equalsIgnoreCase("contract")) {
			return new Contract();
		} else if (type.equalsIgnoreCase("match")) {
			return new Match();
		} else if (type.equalsIgnoreCase("player")) {
			return new Player();
		} else if (type.equalsIgnoreCase("team")) {
			return new Team();
		} else if (type.equalsIgnoreCase("coach")) {
			return new Coach();
		} else if (type.equalsIgnoreCase("matchround")) {
			return new MatchRound();
		} else if (type.equalsIgnoreCase("nationality")) {
			return new Nationality();
		} else if (type.equalsIgnoreCase("playerrole")) {
			return new PlayerRole();
		} else if (type.equalsIgnoreCase("playerposition")) {
			return new PlayerPosition();
		}
		return null;
	}

}
