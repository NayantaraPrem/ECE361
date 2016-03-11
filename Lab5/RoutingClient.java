import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;


import java.net.Socket;

public class RoutingClient {
	@SuppressWarnings("null")
	static int noNodes = 0;
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Socket socket = new Socket("localhost", 9876);
		DataOutputStream socket_out = new DataOutputStream(
				socket.getOutputStream());
		DataInputStream socket_in = new DataInputStream(socket.getInputStream());
		BufferedReader socket_bf = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		Scanner sc = new Scanner(System.in);
		String wts="";	
		//Send num of nodes
		System.out.println("Enter no. of nodes:");
		noNodes = sc.nextInt();
		socket_out.write(noNodes);
		
		//Read weights from the server
		double [][]matrix = new double[noNodes][noNodes];
		String []val;
		wts= socket_bf.readLine();
		//System.out.println(wts);
		val = wts.split(" ", noNodes*noNodes);
		int k = 0;
		//for(int i = 0;i<noNodes*noNodes;i++)
			//System.out.println(val[i]);
		
		//initialize adjacency matrix of delays
		System.out.println("Adjacency Matrix");
		for(int i = 0; i<noNodes;i++){
			for(int j =0; j<noNodes;j++){
				if(val[k] == "Infinity"){
					matrix[i][j] = Double.POSITIVE_INFINITY;
					System.out.print(val[k] + " ");
				} else{
					matrix[i][j] = Double.parseDouble(val[k]); 
					System.out.print(matrix[i][j] + " ");
				}
				k++;
			}
			System.out.println();
		}
		
		//create list of nodes
		List<Node> nodeList = new ArrayList<Node>();
		for(int i = 0; i < noNodes; i++){
			nodeList.add(new Node(i));
		}  

		adjacencyToEdges(matrix, nodeList);
		for(int i = 0; i < noNodes; i++){
			System.out.println("\nNode " + i);
			computePaths(nodeList.get(i), nodeList);
			for(int j = 0;j<noNodes; j++){
				List<Integer> path = getShortestPathTo(nodeList.get(i), nodeList.get(j));
				System.out.print("Total time to reach node " + j+": "+ nodeList.get(j).minDistance+" ms, Path: [");
				System.out.print(path.get(0));
				for(int m = 1; m<path.size();m++)
					System.out.print("," + path.get(m));
				System.out.println("]");
			}
		}
				
		//Close connections
		socket_out.close();
		socket_bf.close();
		socket_in.close();
		sc.close();
		socket.close();
		
}
	

static class Node implements Comparable<Node>
	{
		public final int name;    // Node’s name
		public Edge[] neighbors;  // set of neighbors to this node
		public double minDistance = Double.POSITIVE_INFINITY; //Minimum weight,              											//initially inf
		public Node previous;     // to keep the path
		public Node(int argName)  // constructor to create an instance of this class
		{ 
			name = argName; 
		}

		public int compareTo(Node other)
		{
			return Double.compare(minDistance, other.minDistance);
		}
	}
	
static class Edge
	{
		public final Node target;  // destination node
		public final double weight; // the delay, in ms
		public Edge(Node argTarget, double argWeight) //constructor to create an instance 
		{ 
			target = argTarget;
			weight = argWeight; 
		}
	}	
	
public static void adjacencyToEdges(double[][] matrix, List<Node> v)
{
	for(int i = 0; i < noNodes; i++)
	{
		v.get(i).neighbors = new Edge[noNodes];
		for(int j = 0; j < noNodes; j++)
		{
			v.get(i).neighbors[j] =  new Edge(v.get(j), matrix[i][j]);	
		}
	}
}

public static Comparator<Node> distComp = new Comparator<Node>() {
	 
	public int compare(Node one, Node two) {
		return (int)(one.minDistance-two.minDistance);
	}
};

public static void computePaths(Node source, List<Node>v){	
	PriorityQueue<Node> NodeQueue = new PriorityQueue<Node>(noNodes, distComp);
	
	//init min distance and parent and add all Nodes to the Queue
	for(int i = 0;i < noNodes; i++){
		v.get(i).minDistance = Double.POSITIVE_INFINITY;
		v.get(i).previous = null;
		//NodeQueue.add(v.get(i));
	}
	source.minDistance = 0;
	NodeQueue.add(source);
	source.previous = null;
	
	while(!NodeQueue.isEmpty()){
		// get node with min minDistance and remove it from the Queue
		Node sourceNode = NodeQueue.peek();
		//System.out.println("Processing " + sourceNode.name + " : " + sourceNode.minDistance);
		
		//relax all its neighbours
		for(int j = 0; j < noNodes; j++)
		{   Edge e = v.get(sourceNode.name).neighbors[j];
			if(e.weight != Double.POSITIVE_INFINITY && e.target != sourceNode){
				//its a neighbour
				Node targetNode =e.target;
				Double distThruSrc = sourceNode.minDistance + e.weight;
				NodeQueue.remove(sourceNode);
				if(distThruSrc < targetNode.minDistance){
					targetNode.minDistance = distThruSrc;
					targetNode.previous = sourceNode;
					NodeQueue.add(targetNode);					
				}
			}
		}
	}
	
}

public static List<Integer> getShortestPathTo(Node srcNode, Node targetNode){
	List<Integer> path = new ArrayList<Integer>();
	path.add(targetNode.name);
	
	while(targetNode.previous != null){
		targetNode = targetNode.previous;
		path.add(targetNode.name);
	}
	Collections.reverse(path);
    return path;	
}


}
