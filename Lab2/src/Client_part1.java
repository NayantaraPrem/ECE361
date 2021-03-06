import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
//client
public class Client_part1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// define a (client socket)
			Socket socket = new Socket("localhost", 1020);

			// read lines from socket:
			// define once:
			BufferedReader socket_bf = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			
			//read file name from user stdin
			System.out.println("Enter filename:");
			String stdinput = stdin.readLine();
			FileOutputStream fos;
			DataOutputStream socket_dos = new DataOutputStream(
					socket.getOutputStream());
			DataInputStream socket_dis = new DataInputStream(socket.getInputStream());
			String CRLF = "\r\n";
			//HARD CODING THIS FOR NOW
			//String stdinput = "C:\\Users\\Tara\\workspace\\Lab2\\bin\\testfile.txt";
			socket_dos.writeBytes(stdinput+ CRLF);
			String str;
			while(!socket.isClosed()&&(str= socket_bf.readLine())!=null){
				if(str.compareTo("NO") == 0){
					System.out.println("Server did not find file");
					break;
				}
				else if(str.compareTo("YES") == 0){ //YES
					System.out.println("Server found file");
					System.out.println("Receiving file");
					long start = System.currentTimeMillis();
					// accept file transfer
					byte[] buffer = new byte[1024];
					int read = 0;
					fos = new FileOutputStream(stdinput+"_client");
					while((read = socket_dis.read(buffer)) >= 0) {
						fos.write(buffer, 0, read);
					}
					long end = System.currentTimeMillis();
					File file=new File(stdinput);
					System.out.println("File transfer ("+file.length()+" bytes) complete in "+ (end-start) + "ms.");
					if((end-start)!=0)
						System.out.println("File transfer rate is " + file.length()/(end-start) + " KB/s.");
					socket_dos.close();
					fos.close();
					socket_dis.close();
					socket_bf.close();
				}
				else{System.out.println("Unexpected " + str);}
			}
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
