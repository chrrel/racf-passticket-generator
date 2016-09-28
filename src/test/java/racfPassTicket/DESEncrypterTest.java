package racfPassTicket;

import static org.junit.Assert.*;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import racfPassTicket.DESEncrypter;

public class DESEncrypterTest {
	@Test
	public void encryptShouldEncryptAString() {
		try {
			byte[] keyBytes = DatatypeConverter.parseHexBinary("098d61a85585d2ab");
			SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = factory.generateSecret(new DESKeySpec(keyBytes));
			DESEncrypter encrypter = new DESEncrypter(secretKey);
			
			// encrypted should be (Hex):  1e c3 75 3d 7a 79 e3	ce	
			byte[] expectedResult = {(byte) 0x1e,(byte) 0xC3,(byte) 0x75,(byte) 0x3d,(byte) 0x7a,(byte) 0x79,(byte) 0xe3,(byte) 0xce};	
			byte[] result = encrypter.encrypt("TestByte".getBytes());		
		
			assertArrayEquals(expectedResult, result);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}