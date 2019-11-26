import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileTransferrer {

	/**
	 * Create instance for downloading file
	 * @throws IOException
	 */
	public FileTransferrer() throws IOException {
		// prepare socket and packet
		DatagramSocket socket = new DatagramSocket(7788);
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

		System.out.println("Listening...");
		
		// receive file name
		socket.receive(packet);
		
		String filename = new String(packet.getData(), 0, packet.getLength());

		// receive file size
		socket.receive(packet);
		
		long size = Long.parseLong(new String(packet.getData(), 0, packet.getLength()));
		double sizeK = (double) size / 1024;

		System.out.printf("Downloading file %s (%f)", filename, sizeK);

		// open an file output stream for saving content
		FileOutputStream out = new FileOutputStream(filename);
		while (size > 0) {
			socket.receive(packet);				// receive content

			out.write(packet.getData(), 0, packet.getLength());		// write content to file
			size -= packet.getLength();			// update counter
			
			System.out.print(".");
		}

		// release resources
		out.close();
		socket.close();
		System.out.printf("\nFile download completed. %s (%f KB)\n", filename, sizeK);
	}

	/**
	 * Create instance for transferring file
	 * @param path - file path
	 * @param destIP - destination IP address
	 * @throws IOException
	 */
	public FileTransferrer(String path, String destIP) throws IOException {
		// prepare variables
		int destPort = 7788;
		InetAddress dest = InetAddress.getByName(destIP);
		DatagramSocket socket = new DatagramSocket(0);		// 0 => any available port
		
		byte[] buffer = new byte[1024];
		DatagramPacket packet;

		// check file status
		File file = new File(path);
		if (!file.exists() || file.isDirectory())
			throw new IOException("Invalid file.");

		// send file name
		packet = new DatagramPacket(file.getName().getBytes(), file.getName().length(), dest, destPort);
		socket.send(packet);

		// send file size
		long size = file.length();
		double sizeK = (double) size / 1024;
		String sizeStr = String.valueOf(size);
		
		packet = new DatagramPacket(sizeStr.getBytes(), sizeStr.length(), dest, destPort);
		socket.send(packet);

		// open file input stream for reading content
		FileInputStream in = new FileInputStream(file);

		System.out.print("Transferring data");
		while (size > 0) {
			
			int len = in.read(buffer, 0, 1024);		// read file content
			
			packet = new DatagramPacket(buffer, len, dest, destPort);	// put data to packet
			socket.send(packet);					// send the packet
			
			size -= len;							// update counter
			
			System.out.print(".");
			
			// just want to slow down a bit... 
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		
		// release resources
		in.close();
		socket.close();
		System.out.printf("\nFile transmission completed. %s (%f KB)\n", path, sizeK);
		System.exit(0);
	}

	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				new FileTransferrer();					// launch downloader
			} else {
				new FileTransferrer(args[0], args[1]);	// launch transferrer
			}
		} catch (IOException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
			e.printStackTrace();
			System.err.println("Download File\n\tUsage: java FileTransferrer");
			System.err.println("Transfer File\n\tUsage: java FileTransferrer filePath destIP");
		}
	}

}
