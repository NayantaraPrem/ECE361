import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GoBackNClient {
	static int lastAck =0;
	static long[] timer;
	static int wSize;
	static long end;
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
		//noPackets = 10;
		System.out.println("Enter wsize");
		int wSize = scr.nextInt();
		//wSize =4;
		System.out.println("Enter timeOut");
		int timeOut = scr.nextInt();
		//int timeOut = 12000;
		timer = new long[wSize];
		
		
		//get probability of dropping packets by the server 
		//System.out.println("Enter probability of dropping packets (= 0)");
		//int probError =  scr.nextInt();
		int probError = 0;
		// send no of packets
		socket_out.write(noPackets);
		
		//send probError
		socket_out.write(probError);
		
		// ack listener
		Thread thread=new Thread(new Listener(socket));
		thread.start();
		//send 'sent' i.e packet seq no
		/*
		while(noPackets >= sent){
			System.out.println("Sending packet#" + sent);
			socket_out.write(sent);
			sent++;
		}
		*/
		//sending wSize packets at first
		/*while(sent <= noPackets && sent <= wSize){ //sent all wsize packets
			System.out.println("Sending packet#" + sent);
			System.out.println("Sending " + (sent-1)%wSize);
		    timer[(sent-1) % wSize] = System.currentTimeMillis();
			socket_out.write(sent);
			sent++;
		}
*/
		long start = System.currentTimeMillis();
		while(noPackets > lastAck ){ //not received last Ack
			//System.out.println("..." + (sent - lastAck));
			
			//if (lastAck==1)
				//System.out.println(lastAck);

			
			if(sent <= wSize){
				System.out.println("Sending packet#" + sent);
				//System.out.println("Sending " + (sent-1)%wSize);
			    timer[(sent-1) % wSize] = System.currentTimeMillis();
				socket_out.write(sent);
				sent++;
				continue;
			} 

			if(((sent - lastAck) <= wSize)&&( sent <= noPackets)) {
				
				//send upto sent-lastAck more packets
				System.out.println("Sending packet#" + sent );
			    timer[(sent-1) % wSize] = System.currentTimeMillis();
				socket_out.write(sent);
				sent++;
			}
			
			//if timeout resend all packets in windows
			if((System.currentTimeMillis() - timer[lastAck % wSize]) > timeOut){
				sent = lastAck +1;
				System.out.println("Timeout for " + sent);
			}
			thread.sleep(500);
		}
		//long end2 = System.currentTimeMillis();
		thread.join();
		System.out.println("Done");
		//System.out.println(end + " " + end2);
		System.out.println("Total time taken: " +(end-start)+ "ms");
		socket_out.close();
		scr.close();
		socket.close();
	}
	
    public static void setAckNum(int ackNum){
    	lastAck = ackNum;
    }
    public static void setTimer(int i){
    	timer[i] = System.currentTimeMillis();
    }
    public static int getwSize(){
    	return wSize;
    }
    public static void setEnd(long l){
    	end = l;
    }
}
