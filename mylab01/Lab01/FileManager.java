import java.util.*;
import java.io.*;

public class FileManager {
	
	public static void copy(String sourceName, String destinationName) {
		File sourcefile = new File(sourceName);
		File destinationfile = new File(destinationName);
		while (hasNext()) {
			
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
