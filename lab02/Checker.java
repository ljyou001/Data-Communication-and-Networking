import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;

public class Checker {
	
	public Checker() throws IOException {
		String msg = "Is anyone here?";
		String reply = "I am here!";
		
		DatagramSocket socket = new DatagramSocket(5555);
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("255.255.255.255"), 5555);
		socket.send(packet);
		
		DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
		while(true) {
			System.out.println("Listening...");
			
			socket.receive(receivedPacket);
			String content = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
			
			if (content.equals(reply))
				System.out.println(receivedPacket.getAddress());
			else if (content.equals(msg)) {
				packet = new DatagramPacket(reply.getBytes(), reply.length(), receivedPacket.getAddress(), receivedPacket.getPort());
				socket.send(packet);
			}
		}
	}

	public static void main(String[] args) {
		try {
			new Checker();
		} catch (IOException e) {
			System.err.println("System error: " + e.getMessage());
		}
	}

}
