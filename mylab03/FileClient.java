import java.io.*;
import java.net.*;
import java.util.*;

public class FileClient {
	
	public static void upload(String ip, int port, String filename) throws UnknownHostException, IOException {
		Socket socket = new Socket(ip, port);	
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		File file = new File(filename);
		FileInputStream in = new FileInputStream(file);
		out.writeInt(file.getName().length());
		out.write(file.getName().getBytes());
		long size = file.length();
		out.writeLong(size);
		while (size > 0) {
			byte[] buffer = new byte[1024];
			int len = in.read(buffer, 0, buffer.length);
			out.write(buffer, 0, len);
			size -= len;
		}
		System.out.print("File sent!");
		in.close();
		out.close();
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please input\nServer IP: ");
		String ip = scanner.nextLine().trim();
		System.out.print("Port no: ");
		int port = Integer.parseInt(scanner.nextLine());
		System.out.print("File: ");
		String filename = scanner.nextLine().trim();
		scanner.close();
		try {
			FileClient.upload(ip, port, filename);
		} catch (IOException e) {
			System.out.println("File unable find or access");
		}
	}
}
