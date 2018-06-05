package league.project.exception;

public class TeamDAOException extends Exception{


		private static final long serialVersionUID = -8591674581752272658L;

		public TeamDAOException(String message) {
			super(message);
		}

		public TeamDAOException(String message, Throwable cause) {
			super(message, cause);
		}

		public TeamDAOException(Throwable cause) {
			super(cause);
		}


}
