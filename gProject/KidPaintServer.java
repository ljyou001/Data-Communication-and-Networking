import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class KidPaintServer {
	ArrayList<String> clientAddresses = new ArrayList<String>();
	ArrayList<Integer> ports = new ArrayList<Integer>();
	String studioName = "no name";
	int port = 8801;
	int width = 50;
	int height = 50;
	int[][] data = new int[width][height];			// pixel color data array
	ServerSocket srvSocket;
	ArrayList<Socket> list = new ArrayList<Socket>();
	
	public KidPaintServer() throws IOException {
		
		new Thread(()->{
			try {
				udpserver();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		srvSocket = new ServerSocket(port);

		while (true) {
			System.out.printf("[KidPaintServer] Listening at port %d...\n", port);
			Socket cSocket = srvSocket.accept();

			synchronized (list) {
				list.add(cSocket);
				System.out.printf("[KidPaintServer] Total %d clients are connected.\n", list.size());
			}

			Thread t = new Thread(() -> {
				try {
					serveTCP(cSocket);
				} catch (IOException e) {
					System.err.println("[KidPaintServer] connection dropped.");
				}
				synchronized (list) {
					list.remove(cSocket);
				}
			});
			t.start();
		}
	}
	
	public void udpserver() throws IOException {
		String msg = "[KidPaintServer] KidPaint is Launched";
		String reply = "[KidPaintServer] This is Server of KidPaint";
		
		DatagramSocket socket = new DatagramSocket(0);
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("255.255.255.255"), 0);
		
		DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
		while(true) {
			System.out.println("[KidPaintServer] UDP server has launched for searching clients...");
			
			socket.receive(receivedPacket);
			String content = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
			
			if (content.equals(msg)) {
				packet = new DatagramPacket(reply.getBytes(), reply.length(), receivedPacket.getAddress(), receivedPacket.getPort());
				socket.send(packet);
			}
		}
	}
	
	private void serveTCP(Socket clientSocket) throws IOException {
		byte[] buffer = new byte[1024];
		
		InetAddress cIP = clientSocket.getInetAddress();
		int cPort = clientSocket.getPort();
		
		System.out.printf("[KidPaintServer] Established a connection to host %s:%d\n\n", clientSocket.getInetAddress(), clientSocket.getPort());
		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		
		while(true) {
			int funcType = in.readInt();
			
			if (funcType == 1) {
				
				/*funcType 1 means the client want the name of server(Paint board)
				*Request: 1
				*Response: 1, studioName.length(), name of studio
				*/
				out.writeInt(1);
				out.writeInt(studioName.length());
				out.write(studioName.getBytes(), 0, studioName.length());
				System.out.println("[KidPaintServer] Sent the name of studio " + studioName + " to " + clientSocket.getInetAddress());
				
			}else if(funcType == 2) {
				
				/*funcType 2 means Download data from server
				 * Request: 2
				 * Response: 2, width, height, data in the data[][]
				 */
				out.writeInt(2);
				out.writeInt(width);
				out.writeInt(height);
				for (int i = 0; i < data.length; i++) {
					for (int j = 0; j < data[i].length; j++) {
						out.write(data[i][j]);
					}
				}
				
			}else if(funcType == 3) {
				
				/*funcType 3 means chat
				 * Request: 3, length of msg, message(username + msg)
				 * Response: forwardMsg() - 3, length of message, message(username + msg)
				 */
				int len = in.readInt();
				in.read(buffer, 0, len);
				forwardMsg(buffer, len, cIP, cPort);
				
			}else if(funcType == 4) {
				
				/*funcType 4 means pen draw(paintPixel)
				 * Request: 4, selectedColor, col, row
				 * Response: forwardPen() - 4, selectedColor, col, row
				 */
				int selectedColor = in.readInt();
				int col = in.readInt();
				int row = in.readInt();
				forwardPen(selectedColor, col, row, cIP, cPort);
				
			}else if(funcType == 5) {
				
				/*funcType 5 means bucket(paintArea)
				 * Request: 5, selectedColor, col, row
				 * Response: forwardBucket() - 5, selectedColor, col, row
				 */
				int selectedColor = in.readInt();
				int col = in.readInt();
				int row = in.readInt();
				forwardBucket(selectedColor, col, row, cIP, cPort);
				
			}
		}
	}
	
	private void forwardMsg(byte[] data, int len, InetAddress cIP, int cPort) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				try {
					Socket socket = list.get(i);
					if (socket.getInetAddress() != cIP || socket.getPort() != cPort) {
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						out.writeInt(3);
						out.writeInt(len);
						out.write(data, 0, len);
					}
				} catch (IOException e) {
					// the connection is dropped but the socket is not yet removed.
				}
			}
		}
	}
	
	private void forwardPen(int selectedColor, int col, int row,InetAddress cIP, int cPort) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				try {
					Socket socket = list.get(i);
					if (socket.getInetAddress() != cIP || socket.getPort() != cPort) {
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						out.writeInt(4);
						out.writeInt(selectedColor);
						out.writeInt(col);
						out.writeInt(row);
					}
				} catch (IOException e) {
					// the connection is dropped but the socket is not yet removed.
				}
			}
		}
	}
	
	private void forwardBucket(int selectedColor, int col, int row,InetAddress cIP, int cPort) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				try {
					Socket socket = list.get(i);
					if (socket.getInetAddress() != cIP || socket.getPort() != cPort) {
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						out.writeInt(5);
						out.writeInt(selectedColor);
						out.writeInt(col);
						out.writeInt(row);
					}
				} catch (IOException e) {
					// the connection is dropped but the socket is not yet removed.
				}
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		try {
			new KidPaintServer();
		} catch (IOException e) {
			System.err.println("[KidPaintServer] System error: " + e.getMessage());
		}
	}
	
}
