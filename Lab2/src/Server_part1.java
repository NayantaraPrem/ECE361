import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Server_part1 {
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		BufferedReader reader = null;
		try {
			serverSocket = new ServerSocket(1020);
			while(true){
				Socket server = serverSocket.accept();
				reader = new BufferedReader(new InputStreamReader (server.getInputStream()));
				BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
				DataOutputStream socket_dos = new DataOutputStream(server.getOutputStream());
				String input;
				input = reader.readLine();
				System.out.println("Client requesting filename: " + input);
				//defining a file with a specific name and checking if it exists:
				File file=new File(input);
				/*find current working directory for file location
				 * File testFile = new File("");
				 *
			     *String currentPath = testFile.getAbsolutePath();
			     *System.out.println("current path is: " + currentPath);
			     */
				if (file.exists())
				{   System.out.println("FOUND");
					//server responds YES
					socket_dos.writeBytes("YES");
					socket_dos.writeByte('\n');
					FileInputStream fin = new FileInputStream(file);
					int tot = 0;
					int len = 0;
					int fileSize = fin.available();
					System.out.println("FileSize: " + fileSize);
					byte[] buffer = new byte[1024];
					while ((len = fin.read(buffer)) > 0) {
						socket_dos.write(buffer, 0, len);
					}
					
					fin.close();
					socket_dos.close();	
					System.out.println("File sent.");
				}else
				{
					System.out.println("NOT FOUND");
					//reserver responds NO
					socket_dos.writeBytes("NO");
					socket_dos.writeByte('\n');
				}
				socket_dos.close();
				reader.close();
				stdin.close();
				server.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
