package racfPassTicket;

import static org.junit.Assert.*;

import javax.crypto.SecretKey;

import org.junit.Test;

import racfPassTicket.PassTicketUtils;
import racfPassTicket.exceptions.PassTicketInvalidInputException;

public class PassTicketUtilsTest {
	@Test // valid if left-justified and padded with blanks on the right to a length of 8 bytes
	public void validateUserIdOrApplicationNameShouldReturnAValidString() {
		String result = "";
		try {
			result = PassTicketUtils.validateUserIdOrApplicationName("  user");
			boolean isLeftJustified = result.charAt(0) != ' ';
			boolean hasLength8 = result.length() == 8;
			assertTrue(isLeftJustified && hasLength8);
		} catch (PassTicketInvalidInputException e) {
			fail(e.getMessage());
		}
	}
	
	@Test(expected = PassTicketInvalidInputException.class)
	public void validateUserIdOrApplicationNameShouldThrowAnExceptionIfIputIsNull() throws PassTicketInvalidInputException {
		PassTicketUtils.validateUserIdOrApplicationName(null);
	}
	
	@Test(expected = PassTicketInvalidInputException.class)
	public void validateUserIdOrApplicationNameShouldThrowAnExceptionIfInputIsEmpty() throws PassTicketInvalidInputException {
		PassTicketUtils.validateUserIdOrApplicationName("");
	}

	@Test(expected = PassTicketInvalidInputException.class)
	public void validateUserIdOrApplicationNameShouldThrowAnExceptionIfInputIsLongerThan8Chars() throws PassTicketInvalidInputException {
		PassTicketUtils.validateUserIdOrApplicationName("123456789");
	}
	
	@Test
	public void validateRACFsecuredSignonApplicationKeyShouldReturnAValidSecretKey() {
		try {
			SecretKey key = PassTicketUtils.validateRACFsecuredSignonApplicationKey("09 8d 61 a8 55 85 d2 ab");
			assertTrue(key instanceof SecretKey);
		} catch (PassTicketInvalidInputException e) {
			fail(e.getMessage());
		}
	}
	
	@Test(expected = PassTicketInvalidInputException.class)
	public void validateRACFsecuredSignonApplicationKeyShouldThrowAnExceptionIfInputKeyIsNot16CharactersLong() throws PassTicketInvalidInputException {
		PassTicketUtils.validateRACFsecuredSignonApplicationKey("09 8d 61 a8 55 85 d2 ab WW");
	}	
	
	@Test
	public void xorShouldReturnValidResult() {
		try {
			byte[] input1 = {1,0,1,0,1};
			byte[] input2 = {1,1,0,0,1};
			byte[] expectedResult = {0,1,1,0,0};
			byte[] result = PassTicketUtils.xor(input1, input2);
			assertArrayEquals(expectedResult,result);
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}

	@Test
	public void byteArrayToBitArrayShouldReturnValidResult() {
		byte[] input = {1,123};
		int[] expectedResult = {0,0,0,0,0,0,0,1,0,1,1,1,1,0,1,1};
		int[] result = PassTicketUtils.byteArrayToBitArray(input);
		assertArrayEquals(expectedResult, result);
	}
}