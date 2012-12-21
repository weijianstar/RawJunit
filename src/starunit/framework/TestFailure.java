package starunit.framework;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestFailure extends Object {
	protected Test fFailedTest;
	protected Throwable fThrownException;
	
	/**
	 * Constructs a TestFailure with the given test and exception.
	 */
	public TestFailure(Test failedTest, Throwable thrownException){
		fFailedTest= failedTest;
		fThrownException= thrownException;
	}
	
	/**
	 * Gets the failed test.
	 */
	public Test failedTest() {
	    return fFailedTest;
	}
	
	/**
	 * Gets the thrown exception.
	 */
	public Throwable thrownException() {
	    return fThrownException;
	}
	
	/**
	 * Returns a short description of the failure.
	 */
	public String toString() {
	    StringBuffer buffer= new StringBuffer();
	    buffer.append(fFailedTest + ": " + fThrownException.getMessage());
	    return buffer.toString();
	}
	
	public String trace(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		thrownException().printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}
}
