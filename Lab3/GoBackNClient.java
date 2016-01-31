import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GoBackNClient {
	static int lastAck =0;
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Scanner scr = new Scanner(System.in);
		int noPackets;
		int sent = 1;
		// define a (client socket)
		Socket socket = new Socket("localhost", 9876);
		// read  from socket:
		//DataInputStream socket_in = new DataInputStream(
		//		socket.getInputStream());
		//write to socket
		DataOutputStream socket_out = new DataOutputStream(
				socket.getOutputStream());
		
		//get no. of packets from user
		System.out.println("Enter number of packets");
		noPackets = scr.nextInt();
		
		//get probability of dropping packets by the server 
		System.out.println("Enter probability of dropping packets (= 0)");
		int probError =  scr.nextInt();
		
		// send no of packets
		socket_out.write(noPackets);
		
		//send probError
		socket_out.write(probError);
		
		// ack listener
		Thread thread=new Thread(new Listener(socket));
		thread.start();
		//send 'sent' i.e packet seq no
		
		while(noPackets >= sent){
			System.out.println("Sending packet#" + sent);
			socket_out.write(sent);
			sent++;
		}
		
		thread.join();
		System.out.println("Done");	
		socket_out.close();
		scr.close();
		socket.close();
	}
	
    public static void setAckNum(int ackNum){
    	lastAck = ackNum;
    }
}
