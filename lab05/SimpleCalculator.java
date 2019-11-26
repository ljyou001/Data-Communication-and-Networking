import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class SimpleCalculator extends JFrame {
	private JTextField textField;
	public SimpleCalculator() {
		textField = new JTextField();
		getContentPane().add(textField, BorderLayout.NORTH);
		textField.setColumns(10);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 4));
		
		String[] btnLabels = new String[] {
				"7", "8", "9", "+",
				"4", "5", "6", "-",
				"1", "2", "3", "*",
				"C", "0", "=", "/",
				};
		for(String s : btnLabels) {
			JButton btn = new JButton(s);
			panel.add(btn);
		}
		
		this.setTitle("Simple Calculator");
		this.setSize(new Dimension(300, 300));
		this.setVisible(true);
	}


	public static void main(String[] args) {
		new SimpleCalculator();
	}
}
