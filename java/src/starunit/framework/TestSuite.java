package starunit.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Vector;

public class TestSuite implements Test {
	/**
	 * Counts the number of test cases that will be run by this test.
	 */
	public int countTestCases() {
		int count= 0;
		for (Enumeration e= tests(); e.hasMoreElements(); ) {
			Test test= (Test)e.nextElement();
			count= count + test.countTestCases();
		}
		return count;
	}
	
	/**
	 * Returns the tests as an enumeration
	 */
	public Enumeration tests() {
		return fTests.elements();
	}

	/**
	 * Runs the tests and collects their result in a TestResult.
	 */
	public void run(TestResult result) {
		for (Enumeration e= tests(); e.hasMoreElements(); ) {
	  		if (result.shouldStop() )
	  			break;
			Test test= (Test)e.nextElement();
			runTest(test, result);
		}
	}
	
	public void runTest(Test test, TestResult result) {
		test.run(result);
	}

	/**
	 * Constructs a TestSuite from the given class. Adds all the methods
	 * starting with "test" as test cases to the suite.
	 */
	private String fName;
	private Vector fTests = new Vector(10);

	public TestSuite(final Class theClass) {
		fName = theClass.getName();
		Class superClass = theClass;
		Vector names = new Vector();// 保存方法的名字？
		while (Test.class.isAssignableFrom(superClass)) {
			Method[] methods = superClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				addTestMethod(methods[i], names, theClass);
			}
			superClass = superClass.getSuperclass();
		}
		if (fTests.size() == 0) {
			// addTest(warning("No tests found in "+theClass.getName()));
		}
	}

	private void addTestMethod(Method m, Vector names, Class theClass) {
		String name = m.getName();
		if (names.contains(name))
			return;
		if (!isPublicTestMethod(m)) {
			if (isTestMethod(m)) {
				// addTest(warning("Test method isn't public: "+m.getName()));
			}
			return;
		}
		names.addElement(name);
		addTest(createTest(theClass, name));

	}

	static public Test createTest(Class theClass, String name) {
		Constructor constructor = null;
		try {
			constructor = getTestConstructor(theClass);
		} catch (NoSuchMethodException e) {
			// return
			// warning("Class "+theClass.getName()+" has no public constructor TestCase(String name) or TestCase()");
		}
		Object test = null;
		try {
			if (constructor.getParameterTypes().length == 0) {// 如果构造方法没有参数
				test = constructor.newInstance(new Class[0]);
				if (test instanceof TestCase)
					((TestCase) test).setName(name);
			} else {
				test = constructor.newInstance(new Object[] { name });
			}
		} catch (InstantiationException e) {
			// return(warning("Cannot instantiate test case: "+name+" ("+exceptionToString(e)+")"));
		} catch (InvocationTargetException e) {
			// return(warning("Cannot instantiate test case: "+name+" ("+exceptionToString(e)+")"));
		} catch (IllegalAccessException e) {
			// return(warning("Cannot access test case: "+name+" ("+exceptionToString(e)+")"));
		}
		return (Test) test;
	}

	/**
	 * Gets a constructor which takes a single String as its argument or a no
	 * arg constructor.
	 */
	public static Constructor getTestConstructor(Class theClass)
			throws NoSuchMethodException {
		Class[] args = { String.class };
		try {
			return theClass.getConstructor(args);
		} catch (NoSuchMethodException e) {
			// fall through
		}
		return theClass.getConstructor(new Class[0]);
	}

	private boolean isPublicTestMethod(Method m) {
		return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	}

	/*
	 * 判断测试方法是否没有参数、是否返回值类型为void、是否以test开头
	 */
	private boolean isTestMethod(Method m) {
		String name = m.getName();
		Class[] parameters = m.getParameterTypes();
		Class returnType = m.getReturnType();
		return parameters.length == 0 && name.startsWith("test")
				&& returnType.equals(Void.TYPE);
	}

	/*
	 * Adds a test to the suite
	 */
	public void addTest(Test test) {
		fTests.addElement(test);
	}
}
