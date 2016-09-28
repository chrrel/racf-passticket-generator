package racfPassTicket;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.junit.BeforeClass;
import org.junit.Test;


import racfPassTicket.TimeCoder;
import racfPassTicket.exceptions.PassTicketException;

public class TimeCoderTest {
	private static TimeCoder tc; 
	private static DESEncrypter encrypter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		byte[] keyBytes = DatatypeConverter.parseHexBinary("098d61a85585d2ab");
		SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = factory.generateSecret(new DESKeySpec(keyBytes));
		encrypter = new DESEncrypter(secretKey);
		
		tc = new TimeCoder();
	}
		
	@Test(expected = PassTicketException.class)
	public void timeCoderShouldThrowAnExceptionIfInputIsNull() throws PassTicketException {
		byte[] input = null;
		try {
			tc.timeCoder(input, "userID",encrypter);
		} catch (UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}
	
	@Test(expected = PassTicketException.class)
	public void timeCoderShouldThrowAnExceptionIfInputDoesNotHaveLength4() throws PassTicketException {
		byte[] input = {1,2,3};
		try {
			tc.timeCoder(input, "userID",encrypter);
		} catch (UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
	}
	
	@Test // Compare result with test data from PassTicket documentation
	public void generateTimeCoderPaddingShouldReturnValidResult() {
		try {
			byte[] padding = tc.generateTimeCoderPadding("TOM");			
			String paddingString = DatatypeConverter.printHexBinary(padding);
			assertEquals("E3D6D4555555555555555555", paddingString);
			
			padding = tc.generateTimeCoderPadding("IBMUSER");
			paddingString = DatatypeConverter.printHexBinary(padding);
			assertEquals("C9C2D4E4E2C5D95555555555", paddingString);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test 
	public void permuteShouldReturnValidResult() {
		try {		
			byte[] permutationInput = {1,123};
			byte[] permutationOutput = tc.permute(permutationInput, 1);
			
			byte[] expectedResult = new byte[2];
			expectedResult[0] = (byte) Integer.parseInt("10100011", 2);
			expectedResult[1] = (byte) Integer.parseInt("00101010", 2);	

			assertArrayEquals(expectedResult, permutationOutput);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
	
	@Test(expected = PassTicketException.class)
	public void permuteShouldThrowAnExceptionIfInputIsNull() throws PassTicketException {
		byte[] input = null;
		tc.permute(input,1);
	}
	
	@Test(expected = PassTicketException.class)
	public void permuteShouldThrowAnExceptionIfInputDoesNotHaveLength2() throws PassTicketException {
		byte[] input = {1,2,3};
		tc.permute(input,1);
	}
	
	@Test(expected = PassTicketException.class)
	public void permuteShouldThrowAnExceptionIfRoundNumberIsNotValid() throws PassTicketException {
		byte[] input = {1,123};
		tc.permute(input,7);
	}
}