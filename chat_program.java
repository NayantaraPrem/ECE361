import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class chat_program {

	
	public static void main(String[] args) throws Exception  {
		ServerSocket sock  = new ServerSocket(5888);
		Socket server = sock.accept();
//		DataOutputStream writer=new DataOutputStream (server.getOutputStream());
//		writer.writeBytes("Welcome!");
//		writer.writeByte('\n');
//		
//		
//		
//		InputStreamReader is = new InputStreamReader(server.getInputStream());
//		BufferedReader bf = new BufferedReader(is);
//		String str;
//		Scanner sc= new Scanner(System.in);
//		while((str=bf.readLine())!=null){
//			System.out.println(str);
//			writer.writeBytes(sc.nextLine());
//			writer.writeByte('\n');
		
		
		new Thread(new Mytask_receive(server)).start();
		new Thread(new Mytask_send(server)).start();
		server.close();
		}
		
		
	}
	
	
	 
	 
	
 class Mytask_receive implements Runnable {
	private Socket sk;
	Mytask_receive(Socket s){
		 sk = s;
	 }
	@Override
	public void run() {
		
		try {
			InputStreamReader is = new InputStreamReader(sk.getInputStream());
			BufferedReader bf = new BufferedReader(is);
			String str;
			while((str=bf.readLine())!=null){
			System.out.println(str);
			// TODO Auto-generated method stub
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

 class Mytask_send implements Runnable {
	
	 private Socket sk;
	 Mytask_send(Socket s){
		 sk = s;
	 }
	 
	 @Override
	 public void run() {
		 String str;
		 Scanner sc= new Scanner(System.in);
		 DataOutputStream writer;
		try {
			writer = new DataOutputStream (sk.getOutputStream());
			while(sc.hasNext()){	
				writer.writeBytes(sc.nextLine());
				writer.writeByte('\n');
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
				 
	// TODO Auto-generated method stub
	
		 
	 return;
	}
}