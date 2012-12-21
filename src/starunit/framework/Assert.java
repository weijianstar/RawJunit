package starunit.framework;

/**
 * A set of assert methods.  Messages are only displayed when an assert fails.
 */

public class Assert {
	/**
	 * Protect constructor since it is a static only class
	 */
	protected Assert() {
	}
	
	/**
	 * Fails a test with the given message.
	 */
	static public void fail(String message) {
		throw new AssertionFailedError(message);
	}
	static public void fail(){
		fail(null);
	}
	static public void failNotEquals(String message, Object expected, Object actual){
		fail(format(message,expected,actual));
	}
	static String format(String message, Object expected, Object actual){
		String formatted = "";
		if(message != null)
			formatted = message + "";
		return formatted + "expected : <" + expected + "> but was : <" + actual + ">";
	}
  	/**
   	 * Asserts that two ints are equal.
	 */
  	static public void assertEquals(int expected, int actual) {
  		assertEquals(null, expected, actual);
	}
	/**
	 * Asserts that two ints are equal. If they are not
	 * an AssertionFailedError is thrown with the given message.
	 */
  	static public void assertEquals(String message, int expected, int actual) {
		assertEquals(message, new Integer(expected), new Integer(actual));
  	}
	/**
	 * Asserts that two objects are equal. If they are not
	 * an AssertionFailerError is thrown
	 */
	static public void assertEquals(Object expected, Object actual){
		assertEquals(null,expected,actual);
	}
	static public void assertEquals(String message, Object expected, Object actual){
		if(expected == null && actual == null)
			return;
		if(expected != null && expected.equals(actual))
			return;
		failNotEquals(message, expected, actual);
	}
	/**
	 * Asserts that an object isn't null.
	 */
	static public void assertNotNull(Object object){
		assertNotNull(null,object);
	}
	/**
	 * Asserts that an object isn't null. If it is
	 * an AssertionFailedError is thrown with the given message.
	 */
	static public void assertNotNull(String message, Object object) {
		assertTrue(message, object != null);
	}
	/**
	 * Asserts that a condition is true. If it isn't it throws
	 * an AssertionFailedError with the given message.
	 */
	static public void assertTrue(String message, boolean condition) {
		if (!condition)
			fail(message);
	}
}
