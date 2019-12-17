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
	ArrayList<InetAddress> studiosIP = new ArrayList<InetAddress>();
	int sPort = 8801;
	ArrayList<String> studioName = new ArrayList<String>();
	String studioname;
	
	public Studio(String username) throws IOException {
		this.username = username;
		networkdiscovery();
		if (studiosIP.size() > 0 && studioName.size() > 0 && studiosIP.size() == studioName.size()) {
			callUI();
		}
	}
	
	void networkdiscovery() throws IOException{
		String msg = "KidPaint is Launched";
		String reply = "This is Server of KidPaint";
		
		DatagramSocket socket = new DatagramSocket(5555);
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("255.255.255.255"), 0);
		
		DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
		while(true) {
			System.out.println("[Studio] UDP Server is on for server discovery...");
			socket.send(packet);
			
			socket.receive(receivedPacket);
			String content = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
			
			if (content.equals(reply)) {
				studiosIP.add(receivedPacket.getAddress());
				String serverName = getName(receivedPacket.getAddress());
				System.out.println("[Studio] find a server with [IP address| studio name]: [" 
									+ receivedPacket.getAddress() + "|" + serverName + "]");
				studioName.add(serverName);
			}
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
		
		return studioname;
	}
	
	void callUI() throws IOException {
		UI ui = UI.getInstance();
		ui.setData(new int[50][50], 20);
		ui.username = username;
		ui.sIP = studiosIP.get(0);
		ui.setVisible(true);
	}
}
