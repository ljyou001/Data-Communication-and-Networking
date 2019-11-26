import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SimpleChatClient {
	
	String name;
	
	public SimpleChatClient(String server, int port) throws IOException {
		Socket socket = new Socket(server, port);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		Thread t = new Thread(() -> {
			byte[] buffer = new byte[1024];
			try {
				while (true) {
					int len = in.readInt();
					in.read(buffer, 0, len);
					System.out.println(new String(buffer, 0, len));
				}
			} catch (IOException ex) {
				System.err.println("Connection dropped!");
				System.exit(-1);
			}
		});
		t.start();

		System.out.println("Please input your name:");
		Scanner scanner = new Scanner(System.in);
		name = scanner.nextLine();
		
		System.out.println("Please input messages:");
		while (true) {
			String str = name + ": " + scanner.nextLine();
			out.writeInt(str.length());
			out.write(str.getBytes(), 0, str.length());
		}
	}

	public static void main(String[] args) {
		String s = null;
		int p = 0;
		try {
			s = args[0];
			p = Integer.parseInt(args[1]);
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			System.err.println("Usage: java SimpleChatClient ipaddress portNum");
			System.exit(-1);
		}
		try {
			new SimpleChatClient(s, p);
		} catch (IOException e) {
			System.err.printf("Unable to connect server %s:%d\n", s, p);
			System.exit(-1);
		}
	}
	
}