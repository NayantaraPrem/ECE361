import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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


public class RoutingClient implements Runnable {
	static double delay;
	static String mode;
	static String outputPath;
	static int nodeNum;
	static Socket socket;
	static long timer,startTime;
	static int ackn,lastAck=0;
	static double timeoutInterval = 0.0;
	static boolean resend = false;
	public RoutingClient (Socket s){
		socket=s;
	}
	public static void adjacenyToEdges(double[][] matrix, List<Node> v)
	{
		for(int i = 0; i < matrix.length; i++)
		{
			v.get(i).neighbors = new Edge[matrix.length];
			for(int j = 0; j < matrix.length; j++)
			{
				v.get(i).neighbors[j] =  new Edge(v.get(j), matrix[i][j]);	
			}
		}
	}
	public static void computePaths(Node source)
	{
		// Complete the body of this function
		source.minDistance = 0;
		PriorityQueue<Node> NodeQueue = new PriorityQueue<Node>();
		NodeQueue.add(source);
		Node sourceNode;
		
		sourceNode= NodeQueue.poll();
		while(sourceNode!=null){
			
			int numOfEdges = sourceNode.neighbors.length;
			int count;
			for (count  = 0 ; count < numOfEdges ; count ++){
				Node targetNode = sourceNode.neighbors[count].target;
				//NodeQueue.add(targetNode);
				double disThroughSource = sourceNode.minDistance + sourceNode.neighbors[count].weight;
				if (disThroughSource<targetNode.minDistance){
					NodeQueue.remove(targetNode);
					targetNode.minDistance = disThroughSource;
					targetNode.previous = sourceNode;
					NodeQueue.add(targetNode);
				}
			}
			sourceNode= NodeQueue.poll();
		}
		
	
	}

	public static List<Integer> getShortestPathTo(Node target)
	{
		// Complete the body of this function
		List<Integer> path = new ArrayList<Integer>();
		path.add(target.name);
		Node previous = target.previous;
		while (previous != null){
			path.add(previous.name);
			previous = previous.previous;
		}
		List<Integer> pathReverse = new ArrayList<Integer>();
		int i = path.size()-1 ;
		while (i>=0){
			pathReverse.add(path.get(i));
			i--;
		}
		
		return pathReverse;
		
		
	}
	
	static public double get_timeoutInterval(){
		return timeoutInterval;
	}
 
	static public double get_delay(){
		return delay;
	}
	/**
	 * @param args
	 */
	
	public void run() {
		try{
			System.out.println("Connected to :localhost on:"+socket.getPort());

			//reader and writer:
			BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream())); //for reading lines
			DataOutputStream writer=new DataOutputStream(socket.getOutputStream());	//for writing lines.
			
			
			while(socket!=null && socket.isConnected() && !socket.isClosed()){
				int noNodes;
				// Send noNodes to the server, and read a String from it containing adjacency matrix
				// Complete the code here 
				System.out.println("waiting to receive the number of nodes...");
				String read = reader.readLine();
				noNodes = Integer.valueOf(read);
				nodeNum = noNodes;
				System.out.println("number of nodes:" + nodeNum);
				// Create an adjacency matrix after reading from server
				double[][] matrix = new double[noNodes][noNodes];
				
				// Use StringToenizer to store the values read from the server in matrix
				read = reader.readLine();
				String []tokens = read.split(" ");
						// Complete the code here
				//The nodes are stored in a list, nodeList
				List<Node> nodeList = new ArrayList<Node>();
				double temp;
				int i,j;
				System.out.println("\nAdjacency Matrix:");
				for (i = 0 ; i < noNodes ; i ++){
					nodeList.add(new Node(i));
					for (j = 0 ; j < noNodes ; j ++){
						String tempStr = tokens[i*noNodes+j];
						if (tempStr.equals("Infinity"))
							matrix[i][j]=Double.POSITIVE_INFINITY;
						else 
							matrix[i][j]=Double.parseDouble(tempStr);
						System.out.print(matrix[i][j]+" ");
						
					}
					System.out.println(" ");
				}
				
				// Create edges from adjacency matrix
				adjacenyToEdges(matrix, nodeList);
				
				// Finding shortest path for node0
				int num = 0;
				List<Integer> path =null;
				
				
				Node tempNode = nodeList.get(num);
				System.out.println("\nNode "+tempNode.name);
				computePaths(tempNode);
				int number;
				double totalTime = 0;
				
				for (number = 0 ; number < noNodes;number ++){
					path = getShortestPathTo(nodeList.get(number));
					
					int pathIndex = 0;
					for (;pathIndex<path.size();pathIndex++){
						if (pathIndex!=path.size()-1){
							if (path.get(path.size()-1)==(noNodes-1)&&path.get(0)==0)
								timeoutInterval=nodeList.get(path.get(pathIndex)).neighbors[path.get(pathIndex+1)].weight;
							totalTime+=nodeList.get(path.get(pathIndex)).neighbors[path.get(pathIndex+1)].weight;
							
						}
					}
					
					System.out.print("Total time to reach node "+number+": "+totalTime+" ms,");
					printPath(path);
					delay = totalTime;
					if (path.get(path.size()-1)==(noNodes-1) && path.get(0)==0){
						int n = 0;
						outputPath="[";
						while (n<=path.size()-1){
							if (n!=path.size()-1)
								outputPath+=path.get(n)+",";
							else outputPath+=path.get(n)+"]";
							n++;
						}
						
						
						//System.out.print(outputPath);
					}
					path.clear();
					totalTime=0;
				}
				
	
				//System.out.println("Total er :1time to reach node "+tempNode.name+": ");
				int c = 0;
				//System.out.println(" ");
	
				while(c<noNodes){
					nodeList.get(c).minDistance=Double.POSITIVE_INFINITY;;
					nodeList.get(c).previous=null;
					c++;
				}
				
				writer.writeBytes(outputPath+"\r\n");
				//writer.flush();				
				return;
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	static void printPath(List<Integer> p){
		int i = 0;
		System.out.print("Path: [");
		//outputPath = "Path: [";
		while (i < p.size()-1){
			//if (p.get(p.size()-1)==nodeNum-1&& p.get(0)==0)
				//outputPath+=p.get(i)+"ackn,";
			System.out.print(p.get(i)+",");
			i++;
		}
		//outputPath+=p.get(i)+"]";
		
		System.out.println(p.get(i)+"]");
		//System.out.println("-----"+outputPath+"----");
	}
	public static void setTime(long l){
		
		timer = l;
	}
	public static void setAck(int a){
		ackn = a;
	}
	static void setLastAck(int ack){
		RoutingClient.lastAck = ack;
	}
}


class Node implements Comparable<Node>
{
	public final int name;
	public Edge[] neighbors;
	public double minDistance = Double.POSITIVE_INFINITY;
	public Node previous;     // to keep the path
	public Node(int argName) 
	{ 
		name = argName; 
	}

	public int compareTo(Node other)
	{
		return Double.compare(minDistance, other.minDistance);
	}
}

class Edge
{
	public final Node target;
	public final double weight;
	public Edge(Node argTarget, double argWeight)
	{ 
		target = argTarget;
		weight = argWeight; 
	}
}
