package league.project.exception;

public class MatchDAOException extends Exception{


		private static final long serialVersionUID = -8591674581752272658L;

		public MatchDAOException(String message) {
			super(message);
		}

		public MatchDAOException(String message, Throwable cause) {
			super(message, cause);
		}

		public MatchDAOException(Throwable cause) {
			super(cause);
		}


}
