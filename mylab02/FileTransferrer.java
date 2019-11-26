import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileTransferrer {
	
	public FileTransferrer(int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Listening at port " + port);
		while(true) {
			Socket clientSocket = serverSocket.accept();
			System.out.printf("Connected client (%s:%d)\n", clientSocket.getInetAddress(), clientSocket.getPort());
			new Thread(()-> {
				serve(clientSocket);
			}).start();
		}
	}
	
	private void serve(Socket socket) {
		byte[] buffer = new byte[1024];
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			int nameLen = in.readInt();
			in.read(buffer, 0, nameLen);
			String name = new String(buffer, 0, nameLen);

			System.out.print("Downloading file %s " + name);

			long size = in.readLong();
			System.out.printf("(%d)", size);

			
			File file = new File(name);
			FileOutputStream out = new FileOutputStream(file);

			while(size > 0) {
				int len = in.read(buffer, 0, buffer.length);
				out.write(buffer, 0, len);
				size -= len;
				System.out.print(".");
			}
			System.out.println("\nDownload completed.");
			
			in.close();
			out.close();
		} catch (IOException e) {
			System.err.println("unable to download file.");
		}
	}

	public static void send(String ip, int port, String filename) throws UnknownHostException, IOException {
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
		String filename = "";
		String ip = "";
		int port = 5555;
		try {
			if (args.length == 2)
			{
				filename = args[0];
				ip = args[1];
				try {
					FileTransferrer.send(ip, port, filename);
				} catch (IOException e) {
					System.out.println("File unable find or access");
				}
			}
			else if (args.length == 0)
			{
				try {
					new FileTransferrer(port);
				} catch (IOException e) {
					System.err.println("Unable to start server with port " + port);
				}
			}
			else {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			System.err.println("Invalid parameter");
			System.exit(-1);
		}
		
		
	}

}
