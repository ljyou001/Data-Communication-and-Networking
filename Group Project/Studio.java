import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;

public class Studio extends JFrame{
	private String username;
	InetAddress studiosIP;
	int sPort = 8801;
	String studioName;
	String studioname;
	
	int width;
	int height;
	
	public Studio(String username, InetAddress studiosIP) throws IOException {
		this.username = username;
		this.studiosIP = studiosIP;
		String serverName = getName(studiosIP);
		System.out.println("[Studio] find a server with [IP address| studio name]: [" 
							+ studiosIP + "|" + serverName + "]");
		studioName = serverName;
		if (studiosIP != null && studioName != null) {
			int w = getWidth(studiosIP);
			int h = getHeight(studiosIP);
			System.out.println("[Studio] find a server with [Width|Height]: [" 
					+ w + "|" + h + "]");
			callUIWithSize(w, h);
		}
	}
	
	public Studio(String username, InetAddress studiosIP, int width, int height) throws IOException {
		this.username = username;
		this.studiosIP = studiosIP;
		String serverName = getName(studiosIP);
		System.out.println("[Studio] find a server with [IP address| studio name]: [" 
							+ studiosIP + "|" + serverName + "]");
		studioName = serverName;
		if (studiosIP != null && studioName != null) {
			callUIWithSize(width, height);
		}
	}
	
	private String getName(InetAddress sIP) throws IOException {
		Socket socket = new Socket(sIP, sPort);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		byte[] buffer = new byte[1024];
		out.writeInt(1);
		int funcType = in.readInt();
		
		if (funcType == 1) {
			int len = in.readInt();
			in.read(buffer, 0, len);
			studioname = new String(buffer, 0, len);
		}
		out.close();
		
		return studioname;
	}
	
	private int getWidth(InetAddress sIP) throws IOException {
		int width = 0;
		Socket socket = new Socket(sIP, sPort);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		byte[] buffer = new byte[1024];
		out.writeInt(6);
		int funcType = in.readInt();
		
		if (funcType == 6) {
			width = in.readInt();
		}
		out.close();
		
		return width;
	}
	
	private int getHeight(InetAddress sIP) throws IOException {
		int height = 0;
		Socket socket = new Socket(sIP, sPort);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		byte[] buffer = new byte[1024];
		out.writeInt(7);
		int funcType = in.readInt();
		
		if (funcType == 7) {
			height = in.readInt();
		}
		out.close();
		
		return height;
	}
	
	private void callUIWithSize(int width, int height) throws IOException {
		// TODO Auto-generated method stub
		UI ui = UI.getInstance(studiosIP, username);
		ui.setData(new int[width][height], 20);
		ui.setVisible(true);
	}
}
