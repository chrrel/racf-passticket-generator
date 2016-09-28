/* ###########################################################################
    RACF PassTicket Generator
    This is a JAVA implementation of the RACF PassTicket generator algorithm.
   ########################################################################### */
package racfPassTicket;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.crypto.SecretKey;

import racfPassTicket.exceptions.PassTicketException;
import racfPassTicket.exceptions.PassTicketInvalidInputException;

/**  
 * <p>Implementation of the RACF PassTicket generator algorithm.</p>
 * 
 * <p>The algorithm is specified in 'Security Server RACF Macros and Interfaces - Version 2 Release 1', p. 355
 * (<a href="http://publibz.boulder.ibm.com/epubs/pdf/ich2a300.pdf">http://publibz.boulder.ibm.com/epubs/pdf/ich2a300.pdf</a>, Document Number SA23-2288-00)</p>
 * 
 * @author Christian
 * @version 1.0
 * @since 2016-09-28
 */
public class PassTicketGenerator {
	/**
	 * Generates a PassTicket.
	 * @param userId	The user ID
	 * @param applicationName The application name
	 * @param securedSignonKey The secured signon application key (16 hexadecimal numbers) 
	 * @return The generated PassTicket
	 * @throws PassTicketException
	 * @throws PassTicketInvalidInputException
	 */
	public String generate(String userId, String applicationName, String securedSignonKey) throws PassTicketException, PassTicketInvalidInputException {
		try {
			// step 0: prepare all user input: check if valid and bring into right format
			userId = PassTicketUtils.validateUserIdOrApplicationName(userId);
			applicationName = PassTicketUtils.validateUserIdOrApplicationName(applicationName);
			SecretKey secretKey = PassTicketUtils.validateRACFsecuredSignonApplicationKey(securedSignonKey);
	
			DESEncrypter desEncrypter = new DESEncrypter(secretKey);				
							
			// step 1: encrypt userId with the secured signon key -> result1				
			byte[] result1 = desEncrypter.encrypt(userId.getBytes("CP1047"));
							
			// step 2: XOR result1 with the application name -> result2a, then
			// encrypt result2a with the secured signon application key -> result2
			byte[] result2a = PassTicketUtils.xor(result1, applicationName.getBytes("CP1047"));
			byte[] result2 = desEncrypter.encrypt(result2a);
			
			// step 3: Select the left 4 bytes -> result3
			byte[] result3 = Arrays.copyOfRange(result2, 0, 4);

			// step 4: XOR result3 with the date/time information -> result4
			byte[] timeInfoBytes = PassTicketUtils.getTimeAndDateInformation();
			byte[] result4 = PassTicketUtils.xor(result3, timeInfoBytes);
							
			// step 5: time coder algorithm -> result5
			TimeCoder tc = new TimeCoder();				
			byte[] result5 = tc.timeCoder(result4, userId, desEncrypter);
							
			// step 6: translation to an 8-character string = the PassTicket
			String passTicket = translate(result5);

			return passTicket;
		}
		catch(UnsupportedEncodingException e) {
			throw new PassTicketException("Cannot get bytes in EBCDIC. " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Translates the input byte array into 8 alphanumeric characters (= the PassTicket).
	 * @param result5 Result-5 of the PassTicket algorithm
	 * @return The generated PassTicket
	 */
	protected String translate(byte[] result5) {
		int[] bits = PassTicketUtils.byteArrayToBitArray(result5);		
				
		// Bits 31, 32, 1, 2, 3, and 4 are translated to PassTicket character position 1
		StringBuilder passTicket = new StringBuilder();
		passTicket.append(translateChar(bits, 31));
				
		// Repeat the process: Bits 3-8 -> pos2, 7-12 -> pos 3, ..., 27-32 -> pos 8
		for (int i = 3; i <= 27; i = i+4) {
			passTicket.append(translateChar(bits, i));
		}
		
		return passTicket.toString();
	}

	/**
	 * Creates the binary number represented by the six bits beginning at start, divides it by 36 and uses the remainder as an index into the translation table.
	 * @param bits	The bit array containing Result-5 as an integer array
	 * @param start The number of the bit to start with
	 * @return The translated character
	 */
	protected char translateChar(int[] bits, int start) {
		start = start-1; // bit position numbers start with 1, but array starts with 0 -> subtract 1
		StringBuilder binary = new StringBuilder();

		for (int i = start; i <= start + 5; i++) {
			binary.append(bits[i % 32]); // modulo 32 -> starts with 0 again if max number is reached 
		}

		int charNumber = Integer.parseInt(binary.toString(), 2) % 36;
		final char[] translationTable = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9'};	
		
		return translationTable[charNumber];
	}
}