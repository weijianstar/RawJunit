package starunit.main;

public class Demo {
	public int Division(int a, int b) throws Exception {
		if (b == 0) {
			throw new Exception("��������Ϊ0");
		}
		return a / b;
	}

	public int getMaximum(int array[]) throws Exception {
		if (array == null || array.length == 0) {
			throw new Exception("���鲻��Ϊ��");
		}

		for (int i = 1; i < array.length; i++) {
			if (array[0] < array[i])
				array[0] = array[i];
		}
		return array[0];
	}

	private int add(int a, int b) {
		return a + b;
	}
}
