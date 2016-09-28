package racfPassTicket.example;

import java.util.logging.Logger;

import racfPassTicket.PassTicketGenerator;
import racfPassTicket.exceptions.PassTicketException;
import racfPassTicket.exceptions.PassTicketInvalidInputException;

/** Example demonstrating how to use the PassTicket generator. */
public class Main {
	private static final Logger LOG =  Logger.getLogger("passTicket");
	/** Executes the PassTicket generator. */
	public static void main(String[] args) {
		try {			
			PassTicketGenerator pt = new PassTicketGenerator();
			String passTicket = pt.generate("USERID", "APPNAME", "A1B2C3D4E5F6A7B8");
			LOG.info("PassTicket: " + passTicket);
		} catch (PassTicketException | PassTicketInvalidInputException e) {
			LOG.severe(e.getMessage());
		}
	}
}