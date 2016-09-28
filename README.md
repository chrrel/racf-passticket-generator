# RACF PassTicket Generator

This project is a prototype implementation of the RACF PassTicket algorithm in Java. RACF PassTickets are one-time passwords that can be used to logon to a z/OS application. 

A PassTicket is valid for 10 minutes and can be used only once. Any later logon attempt with the same PassTicket is going to be rejected (except when replay protection is disabled on z/OS). It consist of 8 alphanumeric characters (A-Z and 0-9): e.g. `QHOAH1TV`, `VSAU0AJR`, `NWB2T1TX`. 

> **Note:**
> This implementation is meant to be a prototype which can be used as a reference for creating own implementations. It is intented to be an addition to the official documentation. Hence the code is written in favour of understandability, not considering possible optimisations and performance.

## The RACF PassTicket Algorithm

The algorithm itself is specified in *Security Server RACF Macros and Interfaces*, Version 2 Release 1, Document Number: SA23-2288-00.

You can find the documentation in the [IBM Knowledge Center](https://www.ibm.com/support/knowledgecenter/SSLTBW_2.1.0/com.ibm.zos.v2r1.icha300/algor.htm) or in the corresponding [PDF document](http://publibz.boulder.ibm.com/epubs/pdf/ich2a300.pdf).

## Project Structure

The code in this repository is structured as follows.

- **/src/main/java/racfPassTicket** - The actual implementation of the PassTicket algorithm.

- **/src/main/java/racfPassTicket.example** - A short example that demonstrates how to use the generator.

- **/src/main/java/racfPassTicket.exceptions** - The exceptions which can be thrown by the generator.

- **/src/test/java/racfPassTicket** - A small set of JUnit tests which mainly focus on helper methods used by the generator.

## How to use the code
The generator code can be used as shown in [`Main.java`](src/racfPassTicket/example/Main.java) in the `racfPassTicket.example` package. 

A new generator object `pt` has to be instantiated. Afterwards the method `generate` can be called with the parameters `userId`, `applicationName` and `securedSignonKey`. The generated PassTicket will be returned as a String.

```java
try {			
	PassTicketGenerator pt = new PassTicketGenerator();
	String passTicket = pt.generate("USERID", "APPNAME", "A1B2C3D4E5F6A7B8");
	LOG.info("PassTicket: " + passTicket);
} catch (PassTicketException | PassTicketInvalidInputException e) {
	LOG.severe(e.getMessage());
}
```

### Gradle

You can use gradle to build, run and test this project.

`gradle build` - Assemble and test this project.

`gradle run` - Generate a new PassTicket by executing [`Main.java`](src/racfPassTicket/example/Main.java) in the `racfPassTicket.example` package.

### Javadoc

The Javadoc documentation for this project can be generated with `gradle javadoc`.

## Implementation Details
The algorithm is documented [here](https://www.ibm.com/support/knowledgecenter/SSLTBW_2.1.0/com.ibm.zos.v2r1.icha300/algor.htm). Nevertheless there are some aspects which shall be clarified.

### DES Encryption
For cryptographic operations the PassTicket algorithm makes use of the Data Encryption Standard (DES). The mode of operation is the Electronic Codebook Mode (ECB) without additional padding.

```java
Cipher ecipher = Cipher.getInstance("DES/ECB/NoPadding");
```

### Character Encoding

The algorithm requires all text to be provided in the Extended Binary Coded Decimal Interchange Code (EBCDIC) character encoding. It uses EBCDIC code page
1047 which provides the full Latin-1 character set. In this implementation the conversion to EBCDIC is done by the generator automatically.

```java
byte[] userIdBytes = userId.getBytes("CP1047");
```

## Evaluating a generated PassTicket

After configuring PassTicket support, Java programs for evaluating (and generating) PassTickets can be executed on z/OS (e.g. via SSH). You can use this to verify if your implementation is correct. Since RACF offers callable services for PassTickets, there is no need to implement the PassTicket algorithm on z/OS.

The following example shows the evaluation Java program. In line 10 the function `evaluate` is called to evaluate the PassTicket passed as an argument for the given user ID and application name.
```java
import com.ibm.eserver.zos.racf.IRRPassTicket;
import com.ibm.eserver.zos.racf.IRRPassTicketEvaluationException;
public class PtEvaluate {
	public static void main(String args[]) {
		IRRPassTicket pt;
		String userId = "USERID";
		String applicationName = "APPNAME";
		try {
			pt = new IRRPassTicket();
			pt.evaluate(userId, applicationName, args[0]);
			System.out.println("PassTicket is valid.");
		} catch (IRRPassTicketEvaluationException e) {
			System.out.println("Evaluation failed. " + e);
		}
	}
}
```
To execute the program on z/OS it can be compiled with `javac`. When executing the program, the PassTicket to be evaluated has to be passed as the first parameter (line 2, replace A1B2C3D4 with the PassTicket).

The main jar implementing the Java PassTicket support for z/OS has to be included in the Java classpath for compilation and execution.

```
javac -cp /usr/include/java_classes/IRRRacf.jar PTEvaluate.java
java -cp /usr/include/java_classes/IRRRacf.jar PTEvaluate.class A1B2C3D4
```

## License

This project is licensed under the terms of the [MIT license](LICENSE).
