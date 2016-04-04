import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Random;

import javax.xml.ws.handler.MessageContext.Scope;

// The network is represented by a graph, that contains nodes and edges


public class MainClient {

	static String mode;
	static String host;
	static String outputPath;
	static int nodeNum;
	static long timer = 0, end;
	static int ackn,lastAck=0;
	static boolean resend = false;
	static int cwnd = 1;
	static int ssthresh = 16;
	static int noPackets = 0;

	/**
	 * @param args
	 * @throws Exception 
	 */

	public static void main(String[] args) throws Exception {
				Socket socket = new Socket("localhost", 9876);
				Scanner input = new Scanner(System.in);
				DataOutputStream writer=new DataOutputStream(socket.getOutputStream()); 
				Thread th = new Thread(new RoutingClient(socket));
		    	th.start();
		    	th.join();
		    	int sent = 1;
					 
				System.out.print("\nEnter the name of the file: ");
				String filename = input.nextLine();
				
				FileInputStream fin = new FileInputStream(filename);
				System.out.println("\nfile length: "+fin.available());		
		    	//why + 1?
				noPackets = (fin.available()/1000) + 1;
				writer.writeBytes(filename +"\r\n");
				writer.writeBytes(noPackets +"\r\n");
				//System.out.println("total num packets: " + noPackets);
				
				double timeoutInterval=RoutingClient.get_delay()*2+200;
				//create array of packets
				byte [][] bytesarr = new byte[noPackets][1004];
				int length[] = new int[noPackets];
				for( int i = 0;i<noPackets;i++){
					bytesarr[i] = ByteBuffer.allocate(1004).putInt(i+1).array(); //lastAck+1
			    	length[i] = fin.read(bytesarr[i],4,1000);
			    	String str = new String(bytesarr[i], "UTF-8");
			    	//System.out.println("PACKET" + (i+1) +  str);
				}
				//System.out.println("Timeout Int: "+timeoutInterval);
				Thread t = new Thread(new Listener(socket));
		    	t.start();
		    	long start = System.currentTimeMillis();
		    	float old_cwnd = 0;
				while (MainClient.lastAck < noPackets){
					if(sent - 1 == lastAck && lastAck!=0){
						System.out.println("last ack:"+lastAck+"\n"+"# of acks received for cwnd of "+old_cwnd);
					}
					if(socket.isClosed()){
						System.out.println("Socket closed prematurely");
						break;
					}
					if((sent -1 == lastAck) &&( sent <= noPackets)){
						//send up to sent-lastAck more packets
						timer = System.currentTimeMillis();
						System.out.println("cwnd= "+cwnd);
						for(int i = 0; i<(int)cwnd && sent <= noPackets ; i++){
							System.out.println("sending packet no:" + sent);
							
					    	writer.write(bytesarr[sent-1],0,  length[sent-1]+4);
							sent++;
					   }
						old_cwnd = cwnd;
						setcwnd();
					}
					//if timeout resend all packets in windows
					else
						if((System.currentTimeMillis() - timer) > timeoutInterval){
							sent = lastAck +1;
							System.out.println("Timeout");
							
							//clear the timer 
							setTimer();
							ssthresh = (int) (cwnd/2);
							cwnd = 1;
						}
					
				Thread.sleep(200);						
				}
				t.join();
				System.out.println("last ack:"+lastAck+"\n"+"# of acks received for cwnd of "+cwnd);
				long tot_time  = (end-start);
				System.out.println("Total time to send all packets: " +tot_time/1000+ " seconds");
				System.out.println("Total time in terms of RTT: " + tot_time/timeoutInterval + " RTT");
				System.out.println( noPackets + " out of "+ noPackets +" have been sent sucessfully");
				socket.close();
			
			System.out.println("Quitting...");
			fin.close();
			
	}

    public static void setTimer(){
    	timer = System.currentTimeMillis();
    }


	public static void setAckNum(int ackNum){
		if(lastAck<ackNum)
			lastAck = ackNum;
    	//clear timers for ack
    	setTimer();
    	
    }
    public static void setcwnd(){
    	if(cwnd < ssthresh){
			cwnd = cwnd * 2 ;
    	}
		else 
			cwnd= cwnd + 1;
    }

    public static void setEnd(long l){
    	end = l;
    }
    public static Boolean isDone(int ack){
    	if(ack == noPackets)
    		return true;
    	else return false;
    	}
}



