import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class Listener implements Runnable{
	Socket socket;
	DataInputStream din;
	public Listener(Socket s) throws Exception{
		socket = s;
		din = new DataInputStream(socket.getInputStream());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		BufferedReader reader=new BufferedReader(new InputStreamReader(din));
		
			
		try{
			String s=null;
		while((s = reader.readLine())!=null){
						
			if(( s!=null) && (!s.equals(""))){
				int ack = Integer.parseInt(s);
				System.out.println("received "+s);
				System.out.println("Received ack no:"+ack);
				
				MainClient.setAckNum(ack);
				MainClient.setEnd(System.currentTimeMillis());
				if(MainClient.isDone(ack))
					return;
		}
			
		}
			
			
		}catch (Exception e){
			System.out.println("Listener Error");
			e.printStackTrace(System.out);
			
		}
	}
}
