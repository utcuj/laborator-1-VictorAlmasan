package league.project.exception;

public class ContractDAOException extends Exception{


		private static final long serialVersionUID = -8591674581752272658L;

		public ContractDAOException(String message) {
			super(message);
		}

		public ContractDAOException(String message, Throwable cause) {
			super(message, cause);
		}

		public ContractDAOException(Throwable cause) {
			super(cause);
		}


}
