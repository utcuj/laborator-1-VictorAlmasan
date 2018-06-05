package league.project.exception;

public class DuplicateEntryException extends Exception{


		private static final long serialVersionUID = -8591674581752272658L;

		public DuplicateEntryException(String message) {
			super(message);
		}

		public DuplicateEntryException(String message, Throwable cause) {
			super(message, cause);
		}

		public DuplicateEntryException(Throwable cause) {
			super(cause);
		}


}
