import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

//exercise 1

public class Calculator extends JFrame {

	public Calculator() {
		this.setTitle("It is my first Java GUI app");
		this.setSize(new Dimension(400, 400));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());

		JTextField txt = new JTextField();
		container.add(txt, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 4));
		container.add(panel, BorderLayout.CENTER);

		JButton b7 = new JButton("7");
		panel.add(b7);
		b7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "7");
			}
		});

		JButton b8 = new JButton("8");
		panel.add(b8);
		b8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "8");
			}
		});
		
		JButton b9 = new JButton("9");
		panel.add(b9);
		b9.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "9");
			}
		});
		
		JButton bplus = new JButton("+");
		panel.add(bplus);

		JButton b4 = new JButton("4");
		panel.add(b4);
		b4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "4");
			}
		});
		
		JButton b5 = new JButton("5");
		panel.add(b5);
		b5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "5");
			}
		});
		
		JButton b6 = new JButton("6");
		panel.add(b6);
		b6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "6");
			}
		});
		
		JButton bminus = new JButton("-");
		panel.add(bminus);

		JButton b1 = new JButton("1");
		panel.add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "1");
			}
		});
		
		JButton b2 = new JButton("2");
		panel.add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "2");
			}
		});
		
		JButton b3 = new JButton("3");
		panel.add(b3);
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "3");
			}
		});
		
		JButton bmulti = new JButton("*");
		panel.add(bmulti);

		JButton bc = new JButton("C");
		panel.add(bc);
		bc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txt.setText("");
			}
		});
		
		JButton b0 = new JButton("0");
		panel.add(b0);
		b0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String textarea = txt.getText();
				txt.setText(textarea + "0");
			}
		});
		
		JButton beq = new JButton("=");
		panel.add(beq);
		JButton bdiv = new JButton("/");
		panel.add(bdiv);

		this.setVisible(true);
	}

	public static void main(String[] args) {
		new Calculator();
	}

}
