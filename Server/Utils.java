import java.util.Random;

public class Utils {
	/**
	 * @author {@link https://www.codespeedy.com/generate-random-hex-color-code-in-java/} 
	 * */
	public static String generateColour () {
		Random obj = new Random();
		int rand_num = obj.nextInt(0xffffff + 1);
		return String.format("#%06x", rand_num);
	}
}