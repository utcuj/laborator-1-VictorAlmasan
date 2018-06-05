package league.project.exception;

public class ApplicationDAOException extends Exception{


		private static final long serialVersionUID = -8591674581752272658L;

		public ApplicationDAOException(String message) {
			super(message);
		}

		public ApplicationDAOException(String message, Throwable cause) {
			super(message, cause);
		}

		public ApplicationDAOException(Throwable cause) {
			super(cause);
		}


}
