import java.io.*;

public class AdvLogReader {
	
	public static void readLog() throws IOException {
		File sourcefile = new File("type.log");
		FileInputStream in = new FileInputStream(sourcefile);
		DataInputStream di = new DataInputStream(in);
		String cmd = "";
		long size = di.available();
		while (size > 0) {
			di.skipBytes(8);
			size = size - 8;
			int cmdLength = di.readInt();
			cmd = "";
			for (int i = 0; i < cmdLength; i++) {
				cmd += di.readByte();
			}
			System.out.println(cmd);
			size = size - cmdLength;
		}
		di.close();
		in.close();
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		readLog();
	}

}
