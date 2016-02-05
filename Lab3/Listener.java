import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Listener implements Runnable{
	// define a (client socket)
	Socket socket;
	// read  from socket:
	DataInputStream socket_in;
    public Listener (Socket s) throws IOException{
    	socket = s;
    	socket_in  = new DataInputStream(socket.getInputStream());
    }
	public void run(){
		int ackn;
		//Listen for acks
		try {
			while((ackn = socket_in.read()) != -1){
			//set ackn
			GoBackNClient.setAckNum(ackn);
			//GoBackNClient.setTimer((ackn-1) % (GoBackNClient.getwSize()));
			System.out.println("Received ack#"+ ackn); 
			GoBackNClient.setEnd(System.currentTimeMillis());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
