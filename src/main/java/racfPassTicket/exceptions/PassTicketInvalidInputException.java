package racfPassTicket.exceptions;

@SuppressWarnings("serial")
/** Thrown when user input is invalid.*/
public class PassTicketInvalidInputException extends Exception {
	public PassTicketInvalidInputException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public PassTicketInvalidInputException(String msg) {
		super(msg);
	}
}