import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Lab1 {
	
	
	public static void main(String[] args) throws Exception  {
		
		long time1,time2;
		Socket socket = new Socket("localhost",58088);
		java.io.BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String read_line;
		read_line = br.readLine();
		System.out.println(read_line+"should be only display the welcome message");
		String str = null;
		Scanner sc= new Scanner(System.in);
		
		while (true){
			str += "repeat";
			str+= "\r\n";
			
			DataOutputStream writer=new DataOutputStream (socket.getOutputStream());
			time1 = System.currentTimeMillis();
			writer.writeBytes(str);
			time2 = System.currentTimeMillis();
			read_line = br.readLine();
			
			
			System.out.println("echo:"+read_line + " and round trip time is: "+(time2-time1));
		}
		//sc.close();
		
//	
//		new Thread(new Mytask_receive(socket)).start();
//		new Thread(new Mytask_send(socket)).start();
		//socket.close();
	}
	
	
	}


