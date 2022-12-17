package name.lowbrain;

import java.io.IOException;
import java.util.Properties;

public class SingletonSample {

	private static SingletonSample singleton = new SingletonSample();

	private String key1;

	private String key2;

	private SingletonSample() {
		Properties props = new Properties();
		try {
			System.out.println(this.getClass().getName());
			props.load(this.getClass().getResourceAsStream(this.getClass().getSimpleName() + ".properties"));
			key1 = props.getProperty("key1");
			key2 = props.getProperty("key2");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SingletonSample getInstance() {
		return singleton;
	}

	public String getKey1() {
		return key1;
	}

	public String getKey2() {
		return key2;
	}

}