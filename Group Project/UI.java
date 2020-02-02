import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.Color;
import javax.swing.border.LineBorder;

enum PaintMode {Pixel, Area};

public class UI extends JFrame {
	private JTextField msgField;
	private JTextArea chatArea;
	private JPanel pnlColorPicker;
	private JPanel paintPanel;
	private JButton tglImport;
	private JButton tglExport;
	private JToggleButton tglPen;
	private JToggleButton tglBucket;
	
	private static UI instance;
	private int selectedColor = -543230; 	//golden
	
	int width = 0;
	int height = 0;
	int[][] data = new int[50][50];			// pixel color data array
	int blockSize = 16;
	PaintMode paintMode = PaintMode.Pixel;
	InetAddress sIP;								//Data need to be passed
	int sPort = 8801;								//Data need to be passed
	String username;								//Data need to be passed
	String studioname;
	
	//Network initial related part start
	Socket socket;
	DataInputStream in;
	DataOutputStream out;
	//Network initial related part end
	
	/**
	 * get the instance of UI. Singleton design pattern.
	 * @return
	 * @throws IOException 
	 */
	
	public static UI getInstance(InetAddress sIP, String username) throws IOException {
		if (instance == null)
			instance = new UI(sIP, username);
		
		return instance;
	}
	
	public static UI getInstance() throws IOException {
		return instance;
	}
	
	/**
	 * private constructor. To create an instance of UI, call UI.getInstance() instead.
	 * @throws IOException 
	 */
	
	
	private UI(InetAddress sIP, String username) throws IOException {
		setTitle("KidPaint");
		
		JPanel basePanel = new JPanel();
		getContentPane().add(basePanel, BorderLayout.CENTER);
		basePanel.setLayout(new BorderLayout(0, 0));
		
		//Network initial related part start
		this.socket = new Socket(sIP, sPort);
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
		
		Thread t = new Thread(() -> {
			try {
				client();
			} catch (IOException ex) {
				System.err.println("[UI] Connection dropped!");
				System.exit(-1);
			}
		});
		t.start();
		
		downloadData();
		//Network initial related part end
		
		paintPanel = new JPanel() {
			
			// refresh the paint panel
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				Graphics2D g2 = (Graphics2D) g; // Graphics2D provides the setRenderingHints method
				
				// enable anti-aliasing
			    RenderingHints rh = new RenderingHints(
			             RenderingHints.KEY_ANTIALIASING,
			             RenderingHints.VALUE_ANTIALIAS_ON);
			    g2.setRenderingHints(rh);
			    
			    // clear the paint panel using black
				g2.setColor(Color.black);
				g2.fillRect(0, 0, this.getWidth(), this.getHeight());
				
				// draw and fill circles with the specific colors stored in the data array
				for(int x=0; x<data.length; x++) {
					for (int y=0; y<data[0].length; y++) {
						g2.setColor(new Color(data[x][y]));
						g2.fillArc(blockSize * x, blockSize * y, blockSize, blockSize, 0, 360);
						g2.setColor(Color.darkGray);
						g2.drawArc(blockSize * x, blockSize * y, blockSize, blockSize, 0, 360);
					}
				}
			}
		};
		
		paintPanel.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}

			// handle the mouse-up event of the paint panel
			@Override
			public void mouseReleased(MouseEvent e) {
				if (paintMode == PaintMode.Area && e.getX() >= 0 && e.getY() >= 0)
					try {
						paintArea(e.getX()/blockSize, e.getY()/blockSize);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		});
		
		paintPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (paintMode == PaintMode.Pixel && e.getX() >= 0 && e.getY() >= 0)
					try {
						paintPixel(e.getX()/blockSize,e.getY()/blockSize);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}

			@Override public void mouseMoved(MouseEvent e) {}
			
		});
		
		paintPanel.setPreferredSize(new Dimension(data.length * blockSize, data[0].length * blockSize));
		
		JScrollPane scrollPaneLeft = new JScrollPane(paintPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		basePanel.add(scrollPaneLeft, BorderLayout.CENTER);
		
		JPanel toolPanel = new JPanel();
		basePanel.add(toolPanel, BorderLayout.NORTH);
		toolPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		tglImport = new JButton("Import");
		toolPanel.add(tglImport);
		tglImport.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileImporter = new JFileChooser();
				fileImporter.setDialogTitle("Specify a file to save");
				int userSelection = fileImporter.showSaveDialog(paintPanel);
				if (userSelection == JFileChooser.APPROVE_OPTION) {
					System.out.println("[UI] " + fileImporter.getSelectedFile());
					File fileToImport = fileImporter.getSelectedFile();
					try {
						DataInputStream inp = new DataInputStream(new FileInputStream(fileToImport));
						int wi = inp.readInt();
						int he = inp.readInt();
						out.writeInt(2);
						out.writeInt(wi);
						out.writeInt(he);
						int[][] paintData = new int[wi][he];
						System.out.print("[UI][Import] Upload: ");
						for (int i = 0; i < wi; i++) {
							for (int j = 0; j < he; j++) {
								paintData[i][j] = inp.readInt();
								out.writeInt(paintData[i][j]);
								System.out.print("(" + paintData[i][j] + "," + i + "," + j + "), ");
							}
						}
						System.out.println();
						setData(paintData, 20);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				//JOptionPane.showMessageDialog(paintPanel, "Oh! your pressed Import!");
				//String input = JOptionPane.showInputDialog("Input sth");
			}
		});
		
		tglExport = new JButton("Export");
		toolPanel.add(tglExport);
		tglExport.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileExporter = new JFileChooser();
				fileExporter.setDialogTitle("Specify a file to save");
				int userSelection = fileExporter.showSaveDialog(paintPanel);
				if (userSelection == JFileChooser.APPROVE_OPTION) {
					System.out.println("[UI] " + fileExporter.getSelectedFile());
					File fileToSave = fileExporter.getSelectedFile();
					try {
						DataOutputStream out = new DataOutputStream(new FileOutputStream(fileToSave));
						out.writeInt(width);
						out.writeInt(height);
						for (int i = 0; i < data.length; i++) {
							for (int j = 0; j < data[i].length; j++) {
								out.writeInt(data[i][j]);
							}
						}
						System.out.println("[UI] Saved as file: " + fileToSave.getAbsolutePath());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
					/**try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
						for (int i = 0; i < data.length; i++) {
							for (int j = 0; j < data[i].length; j++) {
								writer.write(data[i][j]+"\n");
							 }
							writer.close();
						 }
						System.out.println("Saved as file: " + fileToSave.getAbsolutePath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
					
				}
			}
		});
		
		pnlColorPicker = new JPanel();
		pnlColorPicker.setPreferredSize(new Dimension(24, 24));
		pnlColorPicker.setBackground(new Color(selectedColor));
		pnlColorPicker.setBorder(new LineBorder(new Color(0, 0, 0)));

		// show the color picker
		pnlColorPicker.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {
				ColorPicker picker = ColorPicker.getInstance(UI.instance);
				Point location = pnlColorPicker.getLocationOnScreen();
				location.y += pnlColorPicker.getHeight();
				picker.setLocation(location);
				picker.setVisible(true);
			}
			
		});
		
		toolPanel.add(pnlColorPicker);
		
		tglPen = new JToggleButton("Pen");
		tglPen.setSelected(true);
		toolPanel.add(tglPen);
		
		tglBucket = new JToggleButton("Bucket");
		toolPanel.add(tglBucket);
		
		// change the paint mode to PIXEL mode
		tglPen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tglPen.setSelected(true);
				tglBucket.setSelected(false);
				paintMode = PaintMode.Pixel;
			}
		});
		
		// change the paint mode to AREA mode
		tglBucket.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tglPen.setSelected(false);
				tglBucket.setSelected(true);
				paintMode = PaintMode.Area;
			}
		});
		
		JPanel msgPanel = new JPanel();
		
		getContentPane().add(msgPanel, BorderLayout.EAST);
		
		msgPanel.setLayout(new BorderLayout(0, 0));
		
		msgField = new JTextField();	// text field for inputting message
		
		msgPanel.add(msgField, BorderLayout.SOUTH);
		
		// handle key-input event of the message field
		msgField.addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 10) {		// if the user press ENTER
					try {
						onTextInputted(username + ": " + msgField.getText());			//added username
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}				
					msgField.setText("");
				}
			}
			
		});
		
		chatArea = new JTextArea();		// the read only text area for showing messages
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		
		JScrollPane scrollPaneRight = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneRight.setPreferredSize(new Dimension(300, this.getHeight()));
		msgPanel.add(scrollPaneRight, BorderLayout.CENTER);
		
		this.setSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
	}
	
	//For the networking function start	
	private void client() throws IOException {
		byte[] buffer = new byte[1024];
		System.out.printf("[UI] Established a connection to server %s:%d\n\n", socket.getInetAddress(), socket.getPort());
		
		while (true) {
			
			//Here we receive data from server
			
			int funcType = in.readInt();
			
			if (funcType == 1) {
				
				/*funcType 1 means the client want the name of server(Paint board)
				*Request: 1
				*Server Response: 1, studioName.length(), name of studio
				*/
				int len = in.readInt();
				in.read(buffer, 0, len);
				studioname =  String(buffer, 0, len);
				System.out.println("[UI] The studio has been renamed as: " + studioname);
				
			} else if(funcType == 2) {
				
				/*funcType 2 means Download data[][] from server
				 * Request: 2
				 * Response: 2, width, height, data in the data[][]
				 */
				width = in.readInt();
				height = in.readInt();
				int[][] paintData = new int[width][height];
				for (int i = 0; i < paintData.length; i++) {
					for (int j = 0; j < paintData[i].length; j++) {
						paintData[i][j] = in.readInt();
					}
				}
				setData(paintData, 20);
				paintPanel.repaint();
				System.out.println("[UI] Paint Board Data Downloaded from server");
				
			} else if(funcType == 3) {
				
				/*funcType 3 means chat
				 * Request: 3, length of msg, message(username + msg)
				 * Response: forwardMsg() - 3, length of message, message(username + msg)
				 */
				int len = in.readInt();
				in.read(buffer, 0, len);
				String msg = new String(buffer, 0, len);
				onServerTextInputted(msg);
				System.out.println("[UI] Message received from server (" + len + ")");
				
			} else if(funcType == 4) {
				
				/*funcType 4 means pen draw(paintPixel)
				 * Request: 4, selectedColor, col, row
				 * Response: forwardPen() - 4, selectedColor, col, row
				 */
				selectedColor = in.readInt();
				int co = in.readInt();
				int ro = in.readInt();
				server_paintPixel(co, ro);
				System.out.println("[UI] Pen received");
				
			} else if(funcType == 5) {
				
				/*funcType 5 means bucket(paintArea)
				 * Request: 5, selectedColor, col, row
				 * Response: forwardBucket() - 5, selectedColor, col, row
				 */
				selectedColor = in.readInt();
				int co = in.readInt();
				int ro = in.readInt();
				server_paintArea(co, ro);
				System.out.println("[UI] Bucket received");
				
			} 
			
		}
	}
	//For the networking function ended
	private void downloadData() throws IOException {
		out.writeInt(8);
	}
	
	private String String(byte[] buffer, int i, int len) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * it will be invoked if the user selected the specific color through the color picker
	 * @param colorValue - the selected color
	 */
	public void selectColor(int colorValue) {
		SwingUtilities.invokeLater(()->{
			selectedColor = colorValue;
			pnlColorPicker.setBackground(new Color(colorValue));
		});
	}
	
	/**
	 * Networking Related: follow the instruction of server
	 * it will be invoked if the user inputted text in the message field
	 * @param text - user inputted text
	 */
	private void onServerTextInputted(String text) {
		chatArea.setText(chatArea.getText() + text + "\n");
	}
		 
	/**
	 * it will be invoked if the user inputted text in the message field
	 * @param text - user inputted text
	 * @throws IOException 
	 */
	private void onTextInputted(String text) throws IOException {
		chatArea.setText(chatArea.getText() + text + "\n");
		
		//Networking part start
		out.writeInt(3);
		out.writeInt(text.length());
		out.write(text.getBytes(), 0, text.length());
		//Networking part ends
	}
	
	/**
	 * Networking Related: follow the instruction of server
	 * change the color of a specific pixel
	 * @param col, row - the position of the selected pixel
	 */
	public void server_paintPixel(int col, int row) {
		if (col >= data.length || row >= data[0].length) return;
		
		data[col][row] = selectedColor;
		paintPanel.repaint(col * blockSize, row * blockSize, blockSize, blockSize);
	}
	
	/**
	 * change the color of a specific pixel
	 * @param col, row - the position of the selected pixel
	 * @throws IOException 
	 */
	public void paintPixel(int col, int row) throws IOException {
		if (col >= data.length || row >= data[0].length) return;
		
		data[col][row] = selectedColor;
		paintPanel.repaint(col * blockSize, row * blockSize, blockSize, blockSize);
		
		//Networking part start
		out.writeInt(4);
		out.writeInt(selectedColor);
		out.writeInt(col);
		out.writeInt(row);
		//Networking part ends
	}
	
	/**
	 * Networking Related: follow the instruction of server
	 * change the color of a specific area
	 * @param col, row - the position of the selected pixel
	 * @return a list of modified pixels
	 */
	public List server_paintArea(int col, int row) {
		LinkedList<Point> filledPixels = new LinkedList<Point>();

		if (col >= data.length || row >= data[0].length) return filledPixels;

		int oriColor = data[col][row];
		LinkedList<Point> buffer = new LinkedList<Point>();
		
		if (oriColor != selectedColor) {
			buffer.add(new Point(col, row));
			
			while(!buffer.isEmpty()) {
				Point p = buffer.removeFirst();
				int x = p.x;
				int y = p.y;
				
				if (data[x][y] != oriColor) continue;
				
				data[x][y] = selectedColor;
				filledPixels.add(p);
	
				if (x > 0 && data[x-1][y] == oriColor) buffer.add(new Point(x-1, y));
				if (x < data.length - 1 && data[x+1][y] == oriColor) buffer.add(new Point(x+1, y));
				if (y > 0 && data[x][y-1] == oriColor) buffer.add(new Point(x, y-1));
				if (y < data[0].length - 1 && data[x][y+1] == oriColor) buffer.add(new Point(x, y+1));
			}
			paintPanel.repaint();
		}
		return filledPixels;
	}
	
	/**
	 * change the color of a specific area
	 * @param col, row - the position of the selected pixel
	 * @return a list of modified pixels
	 * @throws IOException 
	 */
	public List paintArea(int col, int row) throws IOException {
		LinkedList<Point> filledPixels = new LinkedList<Point>();

		if (col >= data.length || row >= data[0].length) return filledPixels;

		int oriColor = data[col][row];
		LinkedList<Point> buffer = new LinkedList<Point>();
		
		if (oriColor != selectedColor) {
			buffer.add(new Point(col, row));
			
			while(!buffer.isEmpty()) {
				Point p = buffer.removeFirst();
				int x = p.x;
				int y = p.y;
				
				if (data[x][y] != oriColor) continue;
				
				data[x][y] = selectedColor;
				filledPixels.add(p);
	
				if (x > 0 && data[x-1][y] == oriColor) buffer.add(new Point(x-1, y));
				if (x < data.length - 1 && data[x+1][y] == oriColor) buffer.add(new Point(x+1, y));
				if (y > 0 && data[x][y-1] == oriColor) buffer.add(new Point(x, y-1));
				if (y < data[0].length - 1 && data[x][y+1] == oriColor) buffer.add(new Point(x, y+1));
			}
			paintPanel.repaint();
		}
		
		//Networking part start
		out.writeInt(5);
		out.writeInt(selectedColor);
		out.writeInt(col);
		out.writeInt(row);
		//Networking part ends
		
		return filledPixels;
	}
	
	/**
	 * set pixel data and block size
	 * @param data
	 * @param blockSize
	 */
	public void setData(int[][] data, int blockSize) {
		this.data = data;
		this.blockSize = blockSize;
		paintPanel.setPreferredSize(new Dimension (data.length * blockSize,  data[0].length * blockSize));
		paintPanel.repaint();
	}
}
