import java.awt.Point;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KidPaintServer {
	ArrayList<String> clientAddresses = new ArrayList<String>();
	ArrayList<Integer> ports = new ArrayList<Integer>();
	String studioName;
	int port = 8801;
	int[][] data;			// pixel color data array
	ServerSocket srvSocket;
	ArrayList<Socket> list = new ArrayList<Socket>();
	int width;
	int height;
	
	public KidPaintServer() throws IOException {
		this.studioName = "no name";
		this.width = 50;
		this.height = 50;
		
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
	
	public KidPaintServer(String studioName) throws IOException {
		this.studioName = studioName;
		this.width = 50;
		this.height = 50;
	
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
	
	public KidPaintServer(String studioName, int width, int height) throws IOException {
		this.studioName = studioName;
		this.width = width;
		this.height = height;
		this.data = new int[width][height];
	
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
		String msg = "KidPaint is Launched";
		String reply = "This is Server of KidPaint";
		
		DatagramSocket socket = new DatagramSocket(5555);
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("255.255.255.255"), 5556);
		
		DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
		System.out.println("[KidPaintServer] UDP server has launched for searching clients...");
		while(true) {
			
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
				
				/*funcType 2 means Download data from server(triggered by import)
				 * Request: 2
				 * Response: 2, width, height, data in the data[][]
				 */
				width = in.readInt();
				height = in.readInt();
				resetDataSize(width, height);
				System.out.print("[KidPaintServer][2] Received data: ");
				for (int j = 0; j < data.length; j++) {
					for (int k = 0; k < data[j].length; k++) {
						this.data[j][k] = in.readInt();
						System.out.print("(" + data[j][k] + "," + j + "," + k + "), ");
					}
				}
				System.out.println();
				forwardFullData(cIP, cPort);
				System.out.println("[KidPaintServer][2] paint data sent");
				
			}else if(funcType == 3) {
				
				/*funcType 3 means chat
				 * Request: 3, length of msg, message(username + msg)
				 * Response: forwardMsg() - 3, length of message, message(username + msg)
				 */
				int len = in.readInt();
				in.read(buffer, 0, len);
				System.out.print("[KidPaintServer][3] (" + len + ") content: ");
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
				server_paintPixel(col, row, selectedColor);
				System.out.println("[KidPaintServer][4] (" + col + "," + row + ") " + selectedColor);
				
			}else if(funcType == 5) {
				
				/*funcType 5 means bucket(paintArea)
				 * Request: 5, selectedColor, col, row
				 * Response: forwardBucket() - 5, selectedColor, col, row
				 */
				int selectedColor = in.readInt();
				int col = in.readInt();
				int row = in.readInt();
				forwardBucket(selectedColor, col, row, cIP, cPort);
				server_paintArea(col, row, selectedColor);
				System.out.println("[KidPaintServer][5] (" + col + "," + row + ") " + selectedColor);
				
			}else if(funcType == 6) {
				
				/*funcType 6 means get width
				 * Request: 6 
				 * Response: 6, width
				 */
				out.writeInt(6);
				out.writeInt(width);
				System.out.println("[KidPaintServer][6] " + width);
				
			}else if(funcType == 7) {
				
				/*funcType 7 means get height
				 * Request: 7 
				 * Response: 7, height
				 */
				out.writeInt(7);
				out.writeInt(height);
				System.out.println("[KidPaintServer][7] " + height);
				
			}else if(funcType == 8) {
				
				/*funcType 8 means Download data from server(triggered by UI start)
				 * Request: 8
				 * Response: 2, width, height, data in the data[][]
				 */
				forwardOriData(cIP, cPort);
				System.out.println("[KidPaintServer][8] paint data sent");
			}
			
		}
	}
		
	private void forwardOriData(InetAddress cIP, int cPort) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				try {
					Socket socket = list.get(i);
					if (socket.getInetAddress() == cIP && socket.getPort() == cPort
							&& list.size() > 1) {
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						out.writeInt(2);
						out.writeInt(data.length);
						out.writeInt(data[0].length);
						for (int j = 0; j < data.length; j++) {
							for (int k = 0; k < data[j].length; k++) {
								out.writeInt(data[j][k]);
							}
						}
					}
				} catch (IOException e) {
					// the connection is dropped but the socket is not yet removed.
				}
			}
		}
	}
	
	private void forwardFullData(InetAddress cIP, int cPort) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				try {
					Socket socket = list.get(i);
					if (socket.getInetAddress() != cIP || socket.getPort() != cPort) {
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						out.writeInt(2);
						out.writeInt(width);
						out.writeInt(height);
						for (int j = 0; j < data.length; j++) {
							for (int k = 0; k < data[j].length; k++) {
								out.writeInt(data[j][k]);
							}
						}
					}
				} catch (IOException e) {
					// the connection is dropped but the socket is not yet removed.
				}
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
						System.out.print(new String(data, 0, len));
					}
				} catch (IOException e) {
					// the connection is dropped but the socket is not yet removed.
				}
			}
		}
		System.out.println();
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
	
	/**
	 * Networking Related: follow the instruction of server
	 * change the color of a specific pixel
	 * @param col, row - the position of the selected pixel
	 */
	public void server_paintPixel(int col, int row, int selectedColor) {
		if (col >= data.length || row >= data[0].length) return;
		data[col][row] = selectedColor;
	}
	
	/**
	 * Networking Related: follow the instruction of server
	 * change the color of a specific area
	 * @param col, row - the position of the selected pixel
	 * @return a list of modified pixels
	 */
	public List server_paintArea(int col, int row, int selectedColor) {
		LinkedList<Point> filledPixels = new LinkedList<Point>();

		if (col >= data.length || row >= data[0].length) return filledPixels;

		int oriColor = data[col][row];
		LinkedList<Point> buffer = new LinkedList<Point>();
		
		if (oriColor != selectedColor) {
			buffer.add(new Point(col, row));
			
			while(!buffer.isEmpty()) {
				Point p = buffer.removeFirst();
				int x = p.x;
				int y = p.y;
				
				if (data[x][y] != oriColor) continue;
				
				data[x][y] = selectedColor;
				filledPixels.add(p);
	
				if (x > 0 && data[x-1][y] == oriColor) buffer.add(new Point(x-1, y));
				if (x < data.length - 1 && data[x+1][y] == oriColor) buffer.add(new Point(x+1, y));
				if (y > 0 && data[x][y-1] == oriColor) buffer.add(new Point(x, y-1));
				if (y < data[0].length - 1 && data[x][y+1] == oriColor) buffer.add(new Point(x, y+1));
			}
		}
		return filledPixels;
	}
	
	
	
	public static void main(String[] args) {
		try {
			new KidPaintServer();
		} catch (IOException e) {
			System.err.println("[KidPaintServer] System error: " + e.getMessage());
		}
	}
	
	private void resetDataSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new int[width][height];
	}
	
}
