import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StudioList extends JFrame{
	private String username;
	Set<InetAddress> studio = new HashSet<InetAddress>();
	Set<InetAddress> usedstudio = new HashSet<InetAddress>();
	int width;
	int height;
	InetAddress studiosIP;
	JFrame studioListFrame;
	JPanel studioListPanel;
	int sPort = 8801;
	
	public StudioList(String username) throws IOException, InterruptedException {
		this.username = username;

		//UI starts
		JFrame studioListFrame = new JFrame("Studio List");
		JPanel studioListPanel = new JPanel();
		studioListPanel.setLayout(new FlowLayout());
		JLabel labelUsername = new JLabel(username + ", ");
		JLabel labelNewstudio = new JLabel("want a new studio?");
		JButton tglNewstudio = new JButton();
		tglNewstudio.setText("New Studio");
		tglNewstudio.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					NewStudio newstudio = new NewStudio(username);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JLabel labelStudios = new JLabel("Below here are studios you can choose");
		
		studioListPanel.add(labelUsername);
		studioListPanel.add(labelNewstudio);
		studioListPanel.add(tglNewstudio);
		studioListPanel.add(labelStudios);
		
		studioListFrame.add(studioListPanel);
		
		studioListFrame.setLocationRelativeTo(null);
		
		new Thread(()->{
			try {
				networkdiscovery();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		while(true) {
			for (Iterator<InetAddress> it = studio.iterator(); it.hasNext(); ) {
				InetAddress ip = it.next();
				String f = getName(ip);
				if (!usedstudio.contains(ip)) {
					JButton studioServerIP = new JButton(f);
					studioListPanel.add(studioServerIP);
					usedstudio.add(ip);
					studioServerIP.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								Studio callstudio = new Studio(username, ip);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					});
				}
				TimeUnit.SECONDS.sleep(2);
		    }
			studioListFrame.repaint();
			studioListFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			studioListFrame.setSize(500, 500);
			studioListFrame.setVisible(true);
		}
		//Studio studio = new Studio(username, studiosIP);
	}
	
	private String getName(InetAddress sIP) throws IOException {
		String studioname = "";
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
	
	void networkdiscovery() throws IOException{
		String msg = "KidPaint is Launched";
		String reply = "This is Server of KidPaint";
		
		DatagramSocket socket = new DatagramSocket(5556);
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("255.255.255.255"), 5555);
		
		DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
		System.out.println("[StudioList] UDP Client is on for server discovery...");
		while(true) {
			socket.send(packet);
			
			socket.receive(receivedPacket);
			String content = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
			
			if (content.equals(reply)) {
				InetAddress studiosIP = receivedPacket.getAddress();
				//System.out.println("[StudioList] Server found: " + studiosIP);
				studio.add(studiosIP);
			}
		}
	}
	
}
