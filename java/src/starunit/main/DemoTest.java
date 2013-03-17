package starunit.main;

import java.lang.reflect.Method;

import starunit.framework.Assert;
import starunit.framework.TestCase;

public class DemoTest extends TestCase {
	private Demo d;
	public DemoTest(){
		
	}
	
	public DemoTest(String name){
		super(name);
	}
	public void setUp() {
		d = new Demo();
	}

	public void testDivision() {
		try {
			Assert.assertEquals(1, d.Division(1, 1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.fail();
			e.printStackTrace();

		}
	}

	public void testDivisionByZero() {
		Throwable tx = null;
		try {
			d.Division(6, 0);
			Assert.fail();
		} catch (Exception e) {
			tx = e;
		}
		Assert.assertEquals(Exception.class, tx.getClass());
		Assert.assertEquals("除数不能为0", tx.getMessage());
	}
	
	public void testGetMaximum(){
		int array[] = {1,2,3,4,5};
		try {
			Assert.assertEquals(5, d.getMaximum(array));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.fail();
		}
	}
	
	public void testGetMaximumByNull(){
		Throwable tx = null;
		try{
			d.getMaximum(null);
			Assert.fail();
		}catch(Exception e){
			tx = e;
		}
		Assert.assertEquals(Exception.class, tx.getClass());
		Assert.assertEquals("数组不能为空", tx.getMessage());
	}
	
	public void testAdd(){
		Class<Demo> clazz = Demo.class;
		
		try {
			Method method = clazz.getDeclaredMethod("add", new Class[]{
					Integer.TYPE,Integer.TYPE});
			
			method.setAccessible(true);
			Object result = method.invoke(d, new Object[]{2,3});
			Assert.assertEquals(5, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Assert.fail();
		} 
	}
	
	public static void main(String agrs[]){
		starunit.runner.TestRunner.run(DemoTest.class);
	}
}
