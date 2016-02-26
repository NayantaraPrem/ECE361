import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	static int lastAck = 0;
	static long timer;
	static long end;
	static float cwnd = 1;
	static int ssthresh = 16;
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
		
		
		int timeOut= 1500;
		timer = 0;

		
		// send no of packets
		socket_out.write(noPackets);

		// ack listener
		Thread thread=new Thread(new AckListener(socket));
		thread.start();
		long start = System.currentTimeMillis();
		while(noPackets > lastAck ){ 
	
			if((sent -1 == lastAck) &&( sent <= noPackets)){

				//send upto sent-lastAck more packets
				System.out.println("sent = " + sent+" lastAck = " + lastAck +" cwnd="+ (int)cwnd);
				timer = System.currentTimeMillis();
				for(int i = 0; i<(int)cwnd && sent <= noPackets ; i++){
					System.out.println("Sending #" + sent);
					socket_out.write(sent);
					sent++;
			   }
				Client.setcwnd();
			}
				//if timeout resend all packets in windows
				else
					if((System.currentTimeMillis() - timer) > timeOut){
						sent = lastAck +1;
						System.out.println("Timeout for " + sent + " is " + (System.currentTimeMillis() - timer) + "ms");
						
						//clear the timer 
						setTimer();
						ssthresh = (int) (cwnd/2);
						cwnd = 1;
					}
				
			Thread.sleep(500);	
			}
			
		
		//long end2 = System.currentTimeMillis();
		thread.join();
		System.out.println("Done");
		//System.out.println(end + " " + end2);
		long tot_time  = (end-start);
		System.out.println("Total time taken: " +tot_time/1000+ "s");
		System.out.println("Number of needed RTTS: " + tot_time/timeOut);
		System.out.println("Average transmission rate: " + noPackets/(tot_time/1000)+ " bytes per sec");
		socket_out.close();
		scr.close();
		socket.close();
	}
	
    private static int max(int ssthresh2, int noPackets) {
		if(ssthresh2 > noPackets)
			return ssthresh2;
		else return noPackets;
	}

	public static void setAckNum(int ackNum){
		if(lastAck<ackNum)
			lastAck = ackNum;
    	//clear timers for ack
    	setTimer();
    	
    }
    public static void setcwnd(){
    	if(cwnd < ssthresh)
			cwnd = cwnd * 2 ;
		else 
			cwnd= cwnd + 1;
    }
    public static void setTimer(){
    	timer = System.currentTimeMillis();
    }

    public static void setEnd(long l){
    	end = l;
    }
}
