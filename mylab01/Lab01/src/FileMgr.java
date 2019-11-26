import java.io.*;
import java.util.*;

public class FileMgr {
	public static void readFile(String filename) throws IOException {
		byte[] buffer = new byte[1024];
		File file = new File(filename);
		FileInputStream in = new FileInputStream(file);
		long size = file.length();
		while (size > 0) {
			int len = in.read(buffer);
			size -= len;
			System.out.println(new String(buffer, 0, len));
		}
	}

	public static void writeFile(String filename) throws IOException {
		File file = new File(filename);
		System.out.println("Please enter the content. (enter @@quit to quit)");
		Scanner scanner = new Scanner(System.in);
		FileOutputStream out = new FileOutputStream(file);
		while (true) {
			String str = scanner.nextLine();
			if (str.equals("@@quit"))
				break;
			byte[] buffer = str.getBytes();
			out.write(buffer);
			out.write('\n');
		}
		out.close();
		scanner.close();
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		readFile("FileManager.java");
	}

}
