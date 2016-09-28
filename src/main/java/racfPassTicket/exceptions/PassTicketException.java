package racfPassTicket.exceptions;

@SuppressWarnings("serial")
/** Thrown when an error occurs during PassTicket generation.*/
public class PassTicketException extends Exception {
	public PassTicketException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public PassTicketException(String msg) {
		super(msg);
	}
}