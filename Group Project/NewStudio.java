import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class NewStudio extends JFrame{
	String username;
	String studioname;
	int width;
	int height;
	public NewStudio(String username) throws IOException {
		this.username = username;
		JTextField nameField = new JTextField(20);
	    JTextField width = new JTextField(5);
	    JTextField height = new JTextField(5);

	    JPanel myPanel = new JPanel();
	    myPanel.add(new JLabel("Studio Name:"));
	    myPanel.add(nameField);
	    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
	    myPanel.add(new JLabel("Width:"));
	    myPanel.add(width);
	    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
	    myPanel.add(new JLabel("Height:"));
	    myPanel.add(height);
	    
	    

	    int result = JOptionPane.showConfirmDialog(null, myPanel, 
	    		"Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
	    if (result == JOptionPane.OK_OPTION) {
	    	System.out.println("[NewStudio] Studio Name: " + nameField.getText());
		    System.out.println("[NewStudio] Width:" + width.getText());
		    System.out.println("[NewStudio] Height:" + height.getText());
		    
		    new Thread(()->{
				try {
					KidPaintServer server = new KidPaintServer(nameField.getText(), 
			    			Integer.parseInt(width.getText()), Integer.parseInt(height.getText())); 
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
	    	System.out.println("[NewStudio] New Server Created" );
	    	Studio client = new Studio(username, InetAddress.getLocalHost(), 
	    			Integer.parseInt(width.getText()), Integer.parseInt(height.getText()));
	    	System.out.println("[NewStudio] New Paint Board Studio Created" );
	    }
	}
	
	
}
