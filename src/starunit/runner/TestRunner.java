package starunit.runner;

import java.io.PrintStream;

import starunit.framework.Test;
import starunit.framework.TestResult;
import starunit.framework.TestSuite;

public class TestRunner {
	private ResultPrinter fPrinter;
	public TestRunner(){
		this(System.out);
	}
	public TestRunner(PrintStream writer){
		this(new ResultPrinter(writer));
	}
	/**
	 * Constructs a TestRunner using the given stream for all the output
	 */
	public TestRunner(ResultPrinter printer){
		fPrinter = printer;
	}

	public TestResult doRun(Test test) {
		return doRun(test, false);
	}
	/**
	 * Creates the TestResult to be used for the test run.
	 */
	protected TestResult createTestResult() {
		return new TestResult();
	}
	
	public TestResult doRun(Test suite, boolean wait) {
		TestResult result= createTestResult();
		result.addListener(fPrinter);
		long startTime= System.currentTimeMillis();
		suite.run(result);
		long endTime= System.currentTimeMillis();
		long runTime= endTime-startTime;
		fPrinter.print(result, runTime);

		//pause(wait);
		return result;
	}
	
	/**
	 * Runs a single test and collects its results.
	 * This method can be used to start a test run
	 * from your program.
	 * <pre>
	 * public static void main (String[] args) {
	 *     test.textui.TestRunner.run(suite());
	 * }
	 * </pre>
	 */
	static public TestResult run(Test test) {
		TestRunner runner= new TestRunner();
		return runner.doRun(test);
	}
	
	/**
	 * Runs a suite extracted from a TestCase subclass.
	 */
	static public void run(Class testClass) {
		run(new TestSuite(testClass));
	}
	
}
