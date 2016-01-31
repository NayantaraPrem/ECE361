import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class StopWaitClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner scr = new Scanner(System.in);
		int noPackets;
		int sent = 1;
		int ackn =-1;
		// define a (client socket)
		Socket socket = new Socket("localhost", 9876);
		//write to socket
		DataOutputStream socket_out = new DataOutputStream(
				socket.getOutputStream());
		// read  from socket:
		DataInputStream socket_in =new DataInputStream(socket.getInputStream());
		//get no. of packets from user
		System.out.println("Enter number of packets");
		noPackets = scr.nextInt();
		
		// send no of packets
		socket_out.write(noPackets);
		
		//send packets and wait for acks
		while(noPackets >= sent){
			System.out.println("Sending packet#" + sent);
			socket_out.write(sent);
			//acks
			ackn = socket_in.read();
			if(ackn == sent){
				System.out.println("Ack#" + ackn);
				sent++;
			}
		}
		

	}

}
