import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class AdvLogReader {

	public static void read(String logfile) {
		File file = new File(logfile);
		if (!file.exists()) {
			System.err.println("File doesn't exsit!");
			System.exit(-1);
		}

		try {
			byte[] buffer = new byte[1024];
			DataInputStream in = new DataInputStream(new FileInputStream(file));

			while (in.available() > 0) {
				long timestamp = in.readLong();					
				System.out.print(timestamp + "\t");
				int cmdLen = in.readInt();		// read the length of the command
				in.read(buffer, 0, cmdLen);
				System.out.println(new String(buffer, 0, cmdLen));
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
