package starunit.framework;

public interface TestListener {
	/*
	 * An error occurred
	 */
	public void addError(Test test, Throwable t);
	/*
	 * a failure occurred
	 */
	public void addFailuer(Test test,AssertionFailedError t);
	/*
	 * A test ended.
	 */
	public void endTest(Test test);
	/*
	 * A test started
	 */
	public void startTest(Test test);
}
