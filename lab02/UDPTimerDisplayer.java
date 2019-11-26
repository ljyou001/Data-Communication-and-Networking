import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPTimerDisplayer {
	
	MulticastSocket socket;
	
	public UDPTimerDisplayer() throws IOException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		socket = new MulticastSocket(45454);
		socket.joinGroup(InetAddress.getByName("224.0.0.0"));
		socket.joinGroup(InetAddress.getByName("230.0.0.0"));
	}
	
	public void start() throws IOException {
		DatagramPacket  packet = new DatagramPacket(new byte[1024], 1024);
		
		while(true) {
			System.out.print("> ");
			socket.receive(packet);
			System.out.println(new String(packet.getData(), 0, packet.getLength()));
		}
	}

	public static void main(String[] args) {
		try {
			new UDPTimerDisplayer().start();
		} catch (IOException e) {
			System.err.println("Unable to receive data.\n" + e.getMessage());
		}
	}

}
