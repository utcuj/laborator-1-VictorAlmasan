package league.project.util;

public class ApplicationData {

	// statuses
	public static final String TEAM_ACTIVE = "A";
	public static final String TEAM_INACTIVE = "I";
	public static final String CONTRACT_ACTIVE = "A";
	public static final String CONTRACT_BANKRUPTCY = "B";
	public static final String CONTRACT_CANCELLED = "C";
	public static final String CONTRACT_RENEWED = "R";
	public static final String CONTRACT_TERMINATE = "T";
	public static final String MATCH_PENDING = "P";
	public static final String MATCH_BANKRUPTCY = "B";
	public static final String MATCH_FINISHED = "F";
	public static final String MATCH_UNSCHEDULED = "U";

	// users
	public static final String USER_ADMIN = "admin";
	public static final String USER_USER = "user";
	public static final String USER_USER1 = "user1";
	public static final String USER_USER2 = "user2";
	public static final String USER_USER3 = "user3";

	// start season constraints
	public static final int TEAM_PLAYER_NO = 3;
	public static final int TEAM_GK_NO = 1;

	// contract
	public static final int MIN_DAYS_CONTRACT_PERIOD = 90;

	// player
	public static final int AGE_CONSTRAINTS= 16;

	// match
	public static final int ROUND_DAYS= 2;

	// season
	public static final int SEASON_STARTED=1;
	public static final int SEASON_NOT_STARTED=0;

	// team
	public static final int TEAM_BUDGET_MIN = 10000000;


}
