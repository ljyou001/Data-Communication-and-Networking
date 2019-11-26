import java.io.*;
import java.net.*;
import java.util.*;

public class Checker {

	DatagramSocket socket;
	String myipaddr;

	public Checker() throws IOException {
		socket = new DatagramSocket(5555);
	}

	public void sendMsg(String str, String destIP, int port) throws IOException {
		InetAddress destination = InetAddress.getByName(destIP);
		DatagramPacket packet = new DatagramPacket(str.getBytes(), str.length(), destination, port);
		socket.send(packet);
		System.out.println("Sent[" + destIP + ":" + port + "]: " + str);
	}

	public boolean receiveInitialMsg() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
		socket.receive(packet);
		byte[] data = packet.getData();
		String str = new String(data, 0, packet.getLength());
		String srcAddr = packet.getAddress().toString();
		int srcPort = packet.getPort();

		System.out.println("Initial Msg Received data:\t" + str);
		System.out.println("Initial Msg sent by:\t" + srcAddr);
		System.out.println("Initial Msg via port:\t" + srcPort);
		
		String address = srcAddr.split("/")[1];
		
		if (str.equals("Is anyone here?") && !address.equals("127.0.0.1")) {
			sendMsg("I am here!", address, srcPort);
			return true;
		}
		return false;
	}
	
	public boolean receiveSecondaryMsg() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
		socket.receive(packet);
		byte[] data = packet.getData();
		String str = new String(data, 0, packet.getLength());
		String srcAddr = packet.getAddress().toString();

		if (str.equals("I am here!")) {
			System.out.println("Msg sent by:\t" + srcAddr);
			return true;
		}
		return false;
	}

	public void initialMsg() throws IOException {
		byte[] msg = "Is anyone here?".getBytes();
		InetAddress dest = InetAddress.getByName("255.255.255.255");
		int port = 5555;
		DatagramPacket packet = new DatagramPacket(msg, msg.length, dest, port);
		socket.send(packet);
		System.out.println("Sent \"Is anyone here?\" message");
	}

	public void end() {
		socket.close();
		System.out.println("bye-bye");
	}

	public static void main(String[] args) throws IOException {

		Scanner scanner = new Scanner(System.in);
		Checker checker = new Checker();
		
		checker.initialMsg();
		
		while (true) {
			System.out.println("\nWaiting for initial data...");
			if(checker.receiveInitialMsg()) {
				break;
			}
		}
		while (true) {
			System.out.println("\nWaiting for initial data...");
			if(checker.receiveSecondaryMsg()) {
				break;
			}
		}
		
		
		scanner.close();
		checker.end();

	}

}