import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class PLotReader {
	
	public static void read(String logfile) {
		File file = new File(logfile);
		if (!file.exists()) {
			System.err.println("File doesn't exsit!");
			System.exit(-1);
		}
		
		try {
			byte[] buffer = new byte[1024];
			FileInputStream in = new FileInputStream(file);
			
			while(in.available() > 0) {
				int len = in.read(buffer, 0, buffer.length);
				System.out.print(new String(buffer, 0, len));
			}
			
			in.close();
		} catch (IOException e) {
			System.err.println("Unable to read the log file.");
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Please input the log file name: ");
		read(scanner.nextLine());
		scanner.close();
	}

}
