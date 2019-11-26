import java.util.*;
import java.io.*;

public class FileManager {
	
	public static void copy(String sourceName, String destinationName) {
		byte[] buffer = new byte[1024];
		File sourcefile = new File(sourceName);
		File destinationfile = new File(destinationName);
		
		try {
			FileInputStream in = new FileInputStream(sourcefile);
			FileOutputStream out = new FileOutputStream(destinationfile);
			long size = sourcefile.length();
			while (size > 0) {
				int len = in.read(buffer);
				size -= len;
				out.write(buffer);
			}
			in.close();
			out.close();
		}
		catch(IOException e) {
			  System.out.println("File may not exist or unreadable");
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		System.out.print("Input the source file name: ");
		String sourceName = scanner.nextLine();
		System.out.print("Input the destination file name: ");
		String destinationName = scanner.nextLine();
		scanner.close();
		copy(sourceName, destinationName);
		
	}

}
