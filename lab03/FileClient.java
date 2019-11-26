import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class FileClient {
	
	public static void upload(String serverIP, int port, String filename) {
		File file = new File(filename);
		if (!file.exists() || file.isDirectory()) {
			System.err.println("Invalid file " + filename);
			return;
		}
		
		try {
			Socket socket = new Socket(serverIP, port);
			FileInputStream in = new FileInputStream(file);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			byte[] buffer = new byte[1024];
			
			out.writeInt(file.getName().length());
			out.write(file.getName().getBytes());
			
			long size = file.length();
			out.writeLong(size);
			
			System.out.printf("Uploading file %s (%d)", file.getName(), size);
			while(size > 0) {
				int len = in.read(buffer, 0, buffer.length);
				out.write(buffer, 0, len);
				size -= len;
				System.out.print(".");
			}
			
			System.out.println("\nUpload completed.");
			in.close();
			out.close();		// the socket will be closed too.
			
			
		} catch (IOException e) {
			System.err.println("Unable upload file " + filename);
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please input");
		System.out.print("Server IP: ");
		String ip = scanner.nextLine().trim();
		
		System.out.print("Port no:   ");
		int port = Integer.parseInt(scanner.nextLine());
		
		System.out.print("File:      ");
		String filename = scanner.nextLine().trim();
		
		scanner.close();
		
		FileClient.upload(ip, port, filename);
		
	}

}
