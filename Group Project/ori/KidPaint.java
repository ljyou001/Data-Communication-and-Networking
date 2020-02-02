import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class KidPaint extends JFrame{
	private String username;
	
	public KidPaint() throws IOException, InterruptedException{
		username = JOptionPane.showInputDialog("Your username");
		System.out.println("[KidPaint Main] Username: " + username);
		StudioList studio = new StudioList(username);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		/**try {
			new KidPaint();
		} catch (IOException e) {
			System.err.println("System error: " + e.getMessage());
		}*/
		KidPaint kidP = new KidPaint();
	}
}
