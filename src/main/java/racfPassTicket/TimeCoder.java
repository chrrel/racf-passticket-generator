package racfPassTicket;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import racfPassTicket.exceptions.PassTicketException;

/** Implementation of the time-coder algorithm. */
public class TimeCoder {
	/**
	 * Executes the time-coder algorithm.
	 * @param input The time-coder input (Result-4)
	 * @param userId The user ID. 
	 * @param desEncrypter An instance of the DES encrypter
	 * @return The result of the time-coder algorithm.
	 * @throws UnsupportedEncodingException
	 * @throws PassTicketException
	 */
	protected byte[] timeCoder(byte[] input, String userId, DESEncrypter desEncrypter) throws UnsupportedEncodingException, PassTicketException {
		if (input == null || input.length != 4) {
			throw new PassTicketException("Time coder input must be 4 bytes.");
		}
		
		// step A: Separate the input (result4) into two portions, left and right side 
		byte[] l2b = Arrays.copyOfRange(input, 0, 2); // 3rd parameter is exclusive
		byte[] r2b = Arrays.copyOfRange(input, 2, 4);

		// step B: Concatenate R2B with 6 bytes of padding bits
		byte[] padding = generateTimeCoderPadding(userId); 
		byte[] pad1 = Arrays.copyOfRange(padding, 0, 6);
		byte[] pad2 = Arrays.copyOfRange(padding, 6, 12);

		for (int i = 1; i <= 6; i++) { // 6 rounds		
			byte[] resultB = new byte[8];
			System.arraycopy(r2b, 0, resultB, 0, 2);
			
			// use pad1 in rounds 1,3 and 5; use pad 2 in rounds 2,4 and 6
			if(i == 1 || i == 3 || i == 5) {
				System.arraycopy(pad1, 0, resultB, 2,  6);
			}
			else {
				System.arraycopy(pad2, 0, resultB, 2,  6);
			}
			
			// step C: encrypt resultB with the secured signon key -> resultC
			byte[] resultC = desEncrypter.encrypt(resultB);
			
			// step D: isolate left 2 bytes, discard the rest
			byte[] resultD =  Arrays.copyOfRange(resultC, 0, 2);
			
			// step E: XOR resultD with l2b  
			byte[] resultE = PassTicketUtils.xor(resultD, l2b);
			
			// step F: Redefine values of l2b and r2b
			l2b = r2b;
			r2b = resultE;
			
			// step G: permute r2b using the permutation tables
			r2b = permute(r2b, i);
			
			// step H: leave the loop after 6 rounds
		}
		
		// step I: recombine l2b and r2b to a 32 bit string -> result5
		byte[] result5 = {l2b[0], l2b[1], r2b[0], r2b[1]};
		
		return result5;
	}	
	
	/**
	 * Generates the padding used in the time-coder process. 
	 * @param userId The user ID for which a PassTicket is generated
	 * @return A 12 byte string consisting of the user ID left justified and padded to the right with hexadecimal '55's
	 * @throws UnsupportedEncodingException
	 */
	protected byte[] generateTimeCoderPadding(String userId) throws UnsupportedEncodingException {
		byte[] userIdBytes = userId.trim().getBytes("CP1047");
		byte[] padding = new byte[12];
		int i;
		for (i = 0; i < userIdBytes.length; i++) {
			padding[i] = userIdBytes[i];
		}
		while (i < padding.length) {
			padding[i] = (byte) 0x55;
			i++;
		}
		return padding;
	}
	
	/**
	 * Permutes input using the permutation table belonging to the current round number.
	 * @param input	The byte array to permute
	 * @param roundNumber The current round number
	 * @return The permuted byte array
	 * @throws PassTicketException
	 */
	protected byte[] permute(byte[] input, int roundNumber) throws PassTicketException{
		if (input == null || input.length != 2) {
			throw new PassTicketException("Permutation input must be 2 bytes.");
		}
		else if(roundNumber < 1 || roundNumber > 6 ) {
			throw new PassTicketException("The round number must be between 1 and 6.");
		}
		
		// transform the two bytes into an array of bits
		int[] inputBits =  PassTicketUtils.byteArrayToBitArray(input);

		// For each round of the time-coder process there is a permutation table = a row in permutationTable[][] (round i -> permutationTable[i-1]).
		int[][] permutationTable = new int[][]{
			{10,2,12,4,14,6,16,8,9,1,11,3,13,5,15,7},
			{1,10,3,12,13,16,7,15,9,2,11,4,5,14,8,6},
			{3,10,1,12,13,16,9,15,7,2,14,4,5,11,8,6},
			{10,4,12,2,14,8,16,6,9,1,13,3,11,5,15,7},
			{4,10,12,1,8,16,14,5,9,2,13,3,11,7,15,6},
			{1,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2}
		};
		
		// create a string containing the permuted bits
		StringBuilder permutedBits  = new StringBuilder();
		for (int i = 0; i < inputBits.length; i++) {
			int inputPosition = permutationTable[roundNumber-1][i] - 1 ; // numbers in permutation table start with 1; array with 0 -> subtract 1
			permutedBits.append(inputBits[inputPosition]);
		}
		
		String firstPermutedByte = permutedBits.toString().substring(0, 8);
		String secondPermutedByte = permutedBits.toString().substring(8, 16);

		byte[] permutedByteArray = new byte[2];
		permutedByteArray[0] = (byte) Integer.parseInt(firstPermutedByte, 2);
		permutedByteArray[1] = (byte) Integer.parseInt(secondPermutedByte, 2);		
			
		return permutedByteArray;
	}
}