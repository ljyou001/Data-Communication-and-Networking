import java.util.*;
import java.io.*;

public class AdvLog {

	public static void log2() throws IOException {
		Scanner scanner = new Scanner(System.in);
		DataOutputStream fo = new DataOutputStream(new FileOutputStream("type.log"));
		while (true) {
			System.out.print("> ");
			String command = scanner.nextLine();
			if (command.equals("exit"))
				break;
			Date d = new Date();
			fo.writeLong(d.getTime());
			fo.writeInt(command.length());
			fo.write(command.getBytes());
		}
		fo.close();
		scanner.close();
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		log2();
	}

}
