package league.project.exception;

public class UserLoginException extends Exception{


		private static final long serialVersionUID = -8591674581752272658L;

		public UserLoginException(String message) {
			super(message);
		}

		public UserLoginException(String message, Throwable cause) {
			super(message, cause);
		}

		public UserLoginException(Throwable cause) {
			super(cause);
		}


}
