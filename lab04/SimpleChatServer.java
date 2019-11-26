import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

class Client {
	String name;
	Socket socket;
}

public class SimpleChatServer {
	ServerSocket srvSocket;
	ArrayList<Client> list = new ArrayList<Client>();

	public SimpleChatServer(int port) throws IOException {
		srvSocket = new ServerSocket(port);

		while (true) {
			System.out.printf("Listening at port %d...\n", port);
			Socket cSocket = srvSocket.accept();

			Client client = new Client();
			client.socket = cSocket;

			synchronized (list) {
				list.add(client);
				System.out.printf("Total %d clients are connected.\n", list.size());
			}

			Thread t = new Thread(() -> {
				try {
					serve(client);
				} catch (IOException e) {
					System.err.println("connection dropped.");
				}
				synchronized (list) {
					list.remove(client);
				}
			});
			t.start();
		}
	}

	private void serve(Client client) throws IOException {
		Socket clientSocket = client.socket;
		byte[] buffer = new byte[1024];
		System.out.printf("Established a connection to host %s:%d\n\n", clientSocket.getInetAddress(),
				clientSocket.getPort());

		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		
		int len = in.readInt();
		in.read(buffer, 0, len);
		client.name = new String(buffer, 0, len);
		
		while (true) {
			len = in.readInt();
			in.read(buffer, 0, len);
			forward(buffer, len, client.name);
		}
	}

	private void forward(byte[] data, int len, String username) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				try {
					Client client = list.get(i);
					if (client.name.equals(username)) continue;
					
					Socket socket = client.socket;
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeInt(len);
					out.write(data, 0, len);
				} catch (IOException e) {
					// the connection is dropped but the socket is not yet removed.
				}
			}
		}
	}
	

	public static void main(String[] args) throws IOException {
		new SimpleChatServer(12345);
	}

}
