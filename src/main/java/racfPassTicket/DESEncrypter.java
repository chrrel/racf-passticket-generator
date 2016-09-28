package racfPassTicket;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import racfPassTicket.exceptions.PassTicketException;

/** Class providing functions for performing DES encryption. */
public class DESEncrypter {
	private Cipher ecipher;

	/**
	 * Instantiates the DES encrypter.
	 * @param key The secret key
	 * @throws PassTicketException
	 */
	protected DESEncrypter(SecretKey key) throws PassTicketException {
		try {
			ecipher = Cipher.getInstance("DES/ECB/NoPadding");
			ecipher.init(Cipher.ENCRYPT_MODE, key);
		}
		catch(Exception e) {
			throw new PassTicketException("Cannot instantiate the DES encrypter", e.getCause());
		}
	}

	/**
	 * Returns the encrypted input.
	 * @param bytesToEncrypt The byte array to be encrypted.
	 * @return A byte array containing the encrypted input.
	 * @throws PassTicketException
	 */
	protected byte[] encrypt(byte[] bytesToEncrypt) throws PassTicketException {
		try {
			return ecipher.doFinal(bytesToEncrypt);
		} catch (Exception e) {
			throw new PassTicketException("Error during encryption.", e.getCause());
		}
	}
}