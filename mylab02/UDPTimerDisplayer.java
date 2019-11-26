import java.io.*;
import java.net.*;

public class UDPTimerDisplayer {

	MulticastSocket socket;

	public UDPTimerDisplayer() throws IOException { 
		 socket = new MulticastSocket(34343); 
	}

	public void receive() throws IOException {
		socket.joinGroup(InetAddress.getByName("224.0.0.0"));
		socket.joinGroup(InetAddress.getByName("230.0.0.0"));
		
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

		socket.receive(packet);

		byte[] data = packet.getData();
		String str = new String(data, 0, packet.getLength());
		int size = packet.getLength();
		String srcAddr = packet.getAddress().toString();
		int srcPort = packet.getPort();

		System.out.println("Received data:\t" + str);
		System.out.println("data size:\t" + size);
		System.out.println("sent by:\t" + srcAddr);
		System.out.println("via port:\t" + srcPort);
		
		socket.leaveGroup(InetAddress.getByName("224.0.0.0"));
		socket.leaveGroup(InetAddress.getByName("230.0.0.0"));
	}

	public static void main(String[] args) throws IOException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		UDPTimerDisplayer receiver = new UDPTimerDisplayer();
		
		while (true) {
			System.out.println("\nWaiting for data...");
			receiver.receive();
		} 
	}

}
