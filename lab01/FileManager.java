import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class FileManager {

	public static void copy(String src, String dst) {
		File srcFile = new File(src);
		if (!srcFile.exists() || srcFile.isDirectory()) {
			System.err.println("Invalid source file " + src);
			System.exit(-1);
		}
		File dstFile = new File(dst);
		if (dstFile.exists() && dstFile.isDirectory()) {
			System.err.println("Cannot write data to a directory " + dst);
			System.exit(-1);
		}

		try {
			FileInputStream in = new FileInputStream(srcFile);
			FileOutputStream out = new FileOutputStream(dstFile);
			long size = srcFile.length();
			byte[] buffer = new byte[1024];
			
			while(size > 0) {
				int len = in.read(buffer);
				out.write(buffer, 0, len);
				size -= len;
			}
			
			in.close();
			out.close();
			
			System.out.println("Done");
			
		} catch (IOException e) {
			System.err.println("Fail to copy data from the source file to the destination file.");
		}

	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Please input the source file name: ");
		String src = scanner.nextLine().trim();
		System.out.print("Please input the destination file name: ");
		String dst = scanner.nextLine().trim();
		
		FileManager.copy(src, dst);
		
		scanner.close();
	}

}
