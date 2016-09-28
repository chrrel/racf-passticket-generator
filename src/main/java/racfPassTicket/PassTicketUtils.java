package racfPassTicket;

import java.math.BigInteger;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.xml.bind.DatatypeConverter;

import racfPassTicket.exceptions.PassTicketInvalidInputException;

/**  Utility class for the PassTicket generator. */
public class PassTicketUtils {
	private PassTicketUtils(){}
	
	/**
	 * Validates a user ID or an application name and returns it in the needed format.
	 * @param str The user ID or the application name.
	 * @return The formatted user ID or application name.
	 * @throws PassTicketInvalidInputException
	 */
	protected static String validateUserIdOrApplicationName(String str) throws PassTicketInvalidInputException {
		if (str == null) {
			throw new PassTicketInvalidInputException("Input cannot be null");
		}
		str = str.replaceAll("^\\s+", ""); // trim left

		if (str.isEmpty()) {
			throw new PassTicketInvalidInputException("Input cannot be empty.");
		} else if (str.getBytes().length > 8) {
			throw new PassTicketInvalidInputException("Input is too long.");
		}
		return String.format("%1$-8s", str); // pad with blanks to the right to a size of 8
	}

	/**
	 * Validates the input and returns a SecretKey.
	 * @param secretKeyString The secret key as a String (16 hexadecimal characters)
	 * @return The SecretKey for the DES encrypter
	 * @throws PassTicketInvalidInputException
	 */
	protected static SecretKey validateRACFsecuredSignonApplicationKey(String secretKeyString) throws PassTicketInvalidInputException {
		secretKeyString = secretKeyString.replaceAll("\\s+", "");
		if (secretKeyString.length() != 16) {
			throw new PassTicketInvalidInputException("Key must be 16 hex characters long.");
		}
		byte[] keyBytes = DatatypeConverter.parseHexBinary(secretKeyString);

		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
			return factory.generateSecret(new DESKeySpec(keyBytes));
		} catch (Exception e) {
			throw new PassTicketInvalidInputException("SecretKey cannot be generated.", e.getCause());
		}
	}

	/**
	 * Retrieve the time and date information (unix time stamp) needed for the PassTicket algorithm.
	 * @return A 4 byte integer representing unix time as byte array
	 */
	protected static byte[] getTimeAndDateInformation() {
		Long currentTime = System.currentTimeMillis() / 1000L; // seconds since 1.1.1970, 00:00 UTC
		String binaryTimeString = Long.toBinaryString(currentTime);		
		BigInteger binaryTimeBi = new BigInteger(binaryTimeString, 2);
		return binaryTimeBi.toByteArray();
	}

	/**
	 * XORs two byte arrays of different or same size.
	 * @param array1 The first array to XOR
	 * @param array2 The second array to XOR
	 * @return The resulting byte array
	 */
	protected static byte[] xor(byte[] array1, byte[] array2) {
		if (array1.length > array2.length) { // make array2 the larger array
			byte[] tmp = array2;
			array2 = array1;
			array1 = tmp;
		}
		for (int i = 0; i < array1.length; i++) {
			array2[i] = (byte) (array1[i] ^ array2[i]);
		}
		return array2;
	}

	/**
	 * Converts a byte array into a bit array (int array).
	 * @param byteArray The byte Array to convert
	 * @return The resulting bit array
	 */
	protected static int[] byteArrayToBitArray(byte[] byteArray) {
		int[] bitArray = new int[byteArray.length * 8];
		for (int i = 0; i < byteArray.length * 8; i++) {
			if ((byteArray[i / 8] & (1 << (7 - (i % 8)))) > 0) {
				bitArray[i] = 1;
			}
		}
		return bitArray;
	}
}