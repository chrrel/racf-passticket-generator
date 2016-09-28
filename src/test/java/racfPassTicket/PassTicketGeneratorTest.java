package racfPassTicket;

import static org.junit.Assert.*;

import javax.xml.bind.DatatypeConverter;
import org.junit.Test;

import racfPassTicket.PassTicketUtils;
import racfPassTicket.PassTicketGenerator;

public class PassTicketGeneratorTest {
	@Test // Compare result with test data from PassTicket documentation
	public void translateShouldReturnValidResult() {
		try {
			PassTicketGenerator pt = new PassTicketGenerator();

			byte[] translationInput = DatatypeConverter.parseHexBinary("07247F79");
			String translationOutput = pt.translate(translationInput);

			assertEquals("QHOAH1TV", translationOutput);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test // Compare result with test data from PassTicket documentation
	public void translateCharShouldReturnValidResult() {
		try {
			PassTicketGenerator pt = new PassTicketGenerator();
			
			byte[] transaltionInput = DatatypeConverter.parseHexBinary("07247F79");			
			int[] bitsToBeTranslated = PassTicketUtils.byteArrayToBitArray(transaltionInput);		
			char translatedChar = pt.translateChar(bitsToBeTranslated, 31);
			
			assertEquals('Q', translatedChar);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}