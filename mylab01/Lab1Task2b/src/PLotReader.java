import java.util.*;
import java.io.*;

public class PLotReader {
	
	public static void readLog() throws FileNotFoundException {
		File file = new File("plaintext.log");
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String cmd = scanner.nextLine();
			String[] splited = cmd.split("\\|\\|",2);
			System.out.println(splited[1]);
		}
		scanner.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		readLog();
	}

}
