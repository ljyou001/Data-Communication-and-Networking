import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class FullCalculator extends JFrame{
	
	int bufferednum = 0;
	String bufferedOperator = "";
	int currentnum = 0;
	boolean operated = false;
	
	public FullCalculator() {
		this.setTitle("Full Calculator");
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
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "7");
			}
		});

		JButton b8 = new JButton("8");
		panel.add(b8);
		b8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "8");
			}
		});
		
		JButton b9 = new JButton("9");
		panel.add(b9);
		b9.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "9");
			}
		});
		
		JButton bplus = new JButton("+");
		panel.add(bplus);
		bplus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				operated = true;
				if (currentnum != 0) {
					bufferednum = currentnum;
				}
				currentnum = Integer.parseInt(txt.getText());
				bufferedOperator = "+";
				if (currentnum != 0 && bufferednum != 0) {
					currentnum = calculating(bufferednum, currentnum, bufferedOperator);
					txt.setText(String.valueOf(currentnum));
				}
			}
		});

		JButton b4 = new JButton("4");
		panel.add(b4);
		b4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "4");
			}
		});
		
		JButton b5 = new JButton("5");
		panel.add(b5);
		b5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "5");
			}
		});
		
		JButton b6 = new JButton("6");
		panel.add(b6);
		b6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "6");
			}
		});
		
		JButton bminus = new JButton("-");
		panel.add(bminus);
		bminus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				operated = true;
				if (currentnum != 0) {
					bufferednum = currentnum;
				}
				currentnum = Integer.parseInt(txt.getText());
				bufferedOperator = "-";
				if (currentnum != 0 && bufferednum != 0) {
					currentnum = calculating(bufferednum, currentnum, bufferedOperator);
					txt.setText(String.valueOf(currentnum));
				}
			}
		});

		JButton b1 = new JButton("1");
		panel.add(b1);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "1");
			}
		});
		
		JButton b2 = new JButton("2");
		panel.add(b2);
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "2");
			}
		});
		
		JButton b3 = new JButton("3");
		panel.add(b3);
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "3");
			}
		});
		
		JButton bmulti = new JButton("*");
		panel.add(bmulti);
		bmulti.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				operated = true;
				if (currentnum != 0) {
					bufferednum = currentnum;
				}
				currentnum = Integer.parseInt(txt.getText());
				bufferedOperator = "*";
				if (currentnum != 0 && bufferednum != 0) {
					currentnum = calculating(bufferednum, currentnum, bufferedOperator);
					txt.setText(String.valueOf(currentnum));
				}
			}
		});

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
				if (operated) {
					txt.setText("");
					operated = false;
				}
				String textarea = txt.getText();
				txt.setText(textarea + "0");
			}
		});
		
		JButton beq = new JButton("=");
		panel.add(beq);
		beq.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				operated = true;
				if (currentnum != 0) {
					bufferednum = currentnum;
				}
				currentnum = Integer.parseInt(txt.getText());
				if (currentnum != 0 && bufferednum != 0) {
					currentnum = calculating(bufferednum, currentnum, bufferedOperator);
					txt.setText(String.valueOf(currentnum));
				}
				currentnum = 0;
			}
		});
		
		JButton bdiv = new JButton("/");
		panel.add(bdiv);
		bdiv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				operated = true;
				if (currentnum != 0) {
					bufferednum = currentnum;
				}
				currentnum = Integer.parseInt(txt.getText());
				bufferedOperator = "/";
				if (currentnum != 0 && bufferednum != 0) {
					currentnum = calculating(bufferednum, currentnum, bufferedOperator);
					txt.setText(String.valueOf(currentnum));
				}
			}
		});
		this.setVisible(true);
	}
	
	public int calculating(int bufferednum, int currentnum, String bufferedOperator) {
		if (bufferedOperator.equals("+")) 
		{
			return bufferednum + currentnum;
		}
		else if(bufferedOperator.equals("-")) 
		{
			return bufferednum - currentnum;
		}
		else if(bufferedOperator.equals("*")) 
		{
			return bufferednum * currentnum;
		}
		else if(bufferedOperator.equals("/"))
		{
			return bufferednum / currentnum;
		}
		return 0;
	}

	public static void main(String[] args) {
		new FullCalculator();
	}
	
}
