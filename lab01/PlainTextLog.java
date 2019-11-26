import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class PlainTextLog {
	
	public static void log1() throws IOException {
		Scanner scanner = new Scanner(System.in);
		FileOutputStream fo = new FileOutputStream("plaintext.log");
		while (true) {
			System.out.print("> ");
			String command = scanner.nextLine();
			if (command.equals("exit"))
				break;
			Date d = new Date();
			String log = String.format("%d||%s\n", d.getTime(), command);
			fo.write(log.getBytes());
		}
		fo.close();
		scanner.close();
	}

	public static void main(String[] args) throws IOException {
		log1();
	}

}
