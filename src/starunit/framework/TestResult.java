package starunit.framework;

import java.util.Enumeration;
import java.util.Vector;

public class TestResult extends Object {
	private boolean fStop;
	protected int fRunTests;
	protected Vector fFailures;
	protected Vector fErrors;
	protected Vector fListeners;
	public TestResult(){
		fFailures = new Vector();
		fErrors = new Vector();
		fListeners = new Vector();
		fRunTests = 0;
		fStop = false;
	}
	/**
	 * Checks whether the test run should stop
	 */
	public synchronized boolean shouldStop() {
		return fStop;
	}
	/**
	 * Registers a TestListener
	 */
	public synchronized void addListener(TestListener listener){
		fListeners.addElement(listener);
	}
	/**
	 * Returns a copy of the listeners.
	 */
	private synchronized Vector cloneListeners() {
		return (Vector)fListeners.clone();
	}
	/**
	 * Adds a failure to the list of failures. The passed in exception
	 * caused the failure.
	 */
	public synchronized void addFailure(Test test,AssertionFailedError t){
		fFailures.addElement(new TestFailure(test,t));
		for(Enumeration e = cloneListeners().elements(); e.hasMoreElements();){
			((TestListener)e.nextElement()).addFailuer(test, t);
		}
	}
	/**
	 * Adds an error to the list of errors. The passed in exception caused the error.
	 */
	public synchronized void addError(Test test, Throwable t){
		fErrors.addElement(new TestFailure(test,t));
		for(Enumeration e = cloneListeners().elements(); e.hasMoreElements();){
			((TestListener)e.nextElement()).addError(test, t);
		}
	}
	/**
	 * Ends a test.
	 */
	public void endTest(Test test){
		for(Enumeration e = cloneListeners().elements(); e.hasMoreElements();){
			((TestListener)e.nextElement()).endTest(test);
		}
	}
	
	/*b
	 * Run a TestCase
	 */
	protected void run(final TestCase test){
		startTest(test);
		Protectable p = new Protectable(){
			public void protect() throws Throwable{
				test.runBare();
			}
		};
		runProtected(test,p);
		
		endTest(test);
	}
	/**
	 * Runs a TestCase
	 */
	public void runProtected(final Test test, Protectable p){
		try{
			p.protect();
		}
		catch(AssertionFailedError e){
			addFailure(test,e);
		}
		catch(ThreadDeath e){
			throw e;
		}catch(Throwable e){
			addError(test,e);
		}
	}
	/*
	 * 
	 */
	public void startTest(Test test){
		final int count = test.countTestCases();
		synchronized(this){
			fRunTests += count;
		}
		for(Enumeration e = cloneListeners().elements();e.hasMoreElements(); ){
			((TestListener)e.nextElement()).startTest(test);
		}
	}
	
	/**
	 * Returns an Enumeration for the errors
	 */
	public synchronized Enumeration errors(){
		return fErrors.elements();
	}
	/**
	 * Gets the number of detected failures
	 */
	public synchronized int failureCount(){
		return fFailures.size();
	}
	
	/**
	 * Returns an Enumeration for the failures
	 */
	public synchronized Enumeration failures() {
		return fFailures.elements();
	}
	
	public synchronized int errorCount(){
		return fErrors.size();
	}
	/**
	 * Returns whether the entire test was successful or not.
	 */
	public synchronized boolean wasSuccessful(){
		return failureCount() == 0 && errorCount() == 0;
	}
	public synchronized int runCount(){
		return fRunTests;
	}
}
