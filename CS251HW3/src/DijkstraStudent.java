import HW3.*;

import java.io.IOException;
import java.util.LinkedList;

public class DijkstraStudent extends DisjkstraAbstract{

	/**
	 * Use this for your own testing purposes. Feel free to change it as you wish, as we will write a different one for grading
	 */
	public static void main(String args[]) {
		try {
			DijkstraStudent solver = new DijkstraStudent();
			int[][] L;
			int startState = 0;
			L = solver.readData("graph2.txt");					
			//you can change the source state as you wish
			solver.runDijkstra(L, startState);

			// display results
			System.out.println("The routes are:");
			for (int i = 0; i < solver.Nodes.length; i++) {
				if(i != startState) {
					solver.displayPath(solver.Nodes[i]);
				}
			}				
			System.out.println("Actually, there is a little green man furiously typing this output.");
			
		} catch (IOException e) {
			System.out.println("There was a problem reading the input file. Please make sure you are giving the right path to the file and that the file is written in the right format");			
		}			
	}
	
	
	/**
	 * Runs Dijskstra's algorithm to compute shortest distances form a given vertex of a graph
	 * @param L: 2D array where the value at index (i,j) holds the cost on the edge from i to j. 
	 * A cost of 0 is representative of a lack of an edge from i to j.
	 * @param pStartIdx : Index of start state of paths that are to be computed.
	 */
	public void runDijkstra(int[][] L, int pStartIdx) {
		//import data to work on
		initializeData(L);				
				
		// initialize priority queue with the start node
		PriorityQueue Q = new PriorityQueue();
        insert(Nodes[pStartIdx], Q);
        Nodes[pStartIdx].dist = 0;   
        
        //while the priority queue is non-empty
        while (!Q.isEmpty()) {
        	//extract the next node for which we know the shortest path
        	GraphNode d  = extractMin(Q);
        	System.out.println("extracting "+d.name);
        	for (GraphNode v : d.neighbours) {
				switch (v.status) {
				case UNPROCESSED: // add to Q					
					v.dist = d.dist + L[d.pos][v.pos];
					v.path = d;
					System.out.println("inserting "+v.name);
					insert(v, Q);
					System.out.println(Q.head.graphNode.name);
					System.out.println(Q.lastInHeap.graphNode.name);
					break;
				case IN_HEAP: // change priority in Q
					System.out.println(v.name+" is in heap, checking for shorter path.");
					int newDist = d.dist + L[d.pos][v.pos];
					if(newDist < v.dist) {	
						v.path = d;
						changePriority(v, newDist);
					}
					break;
				default:
					break;
				}
			}
        }
	}


	@Override
	public void insert(GraphNode pData, PriorityQueue pPQ)
	{
		PQNode aPQNode = new PQNode();
		aPQNode.graphNode = pData;
		aPQNode.graphNode.status = GraphNode.Status.IN_HEAP;
		if(pPQ.isEmpty())
		{
			pPQ.head = aPQNode;
			pPQ.lastInHeap = aPQNode;
		}
		else
		{
			try
			{
				if(pPQ.lastInHeap.parent != null)
				{
					if(pPQ.lastInHeap == pPQ.lastInHeap.parent.left)
					{
						aPQNode.parent = pPQ.lastInHeap.parent;
						pPQ.lastInHeap.parent.right = aPQNode;
						pPQ.lastInHeap.next = aPQNode;
						aPQNode.prev = pPQ.lastInHeap;
						pPQ.lastInHeap = aPQNode;
					}
				else
				{
						//If the last node is not left child it is not trivial to find the next place to insert in the
						//heap. The following while loops find that location.
						PQNode tmp = pPQ.lastInHeap;
						while(tmp.parent != null)
						{
							if(tmp == tmp.parent.right)
							{
								tmp = tmp.parent;
							}
							else
							{
								tmp = tmp.parent.right;
								break;
							}
						}
						while(tmp.left != null)
						{
							tmp = tmp.left;
						}
						tmp.left = aPQNode;
						aPQNode.parent = tmp;
						pPQ.lastInHeap.next = aPQNode;
						aPQNode.prev = pPQ.lastInHeap;
						pPQ.lastInHeap = aPQNode;
					}
					
				}else
				{
					//The case	where lastinheap is the root
					
					aPQNode.parent = pPQ.lastInHeap;
					pPQ.lastInHeap.left = aPQNode;
					pPQ.lastInHeap.next = aPQNode;
					aPQNode.prev = pPQ.lastInHeap;
					pPQ.lastInHeap = aPQNode;
					
				}
			}
			catch(NullPointerException e)
			{

			}
		}
		heapifyUp(pPQ.lastInHeap.graphNode);
	}


	@Override
	public GraphNode extractMin(PriorityQueue pPQ)
	{
		if(pPQ.head == pPQ.lastInHeap)
		{
			GraphNode ret = pPQ.head.graphNode;
			ret.nodeInPQ = null;
			ret.status = GraphNode.Status.KNOWN;
			pPQ.head = null;
			pPQ.lastInHeap = null;
			return ret;
		}
		else
		{
			GraphNode ret = pPQ.head.graphNode;
			ret.status = GraphNode.Status.KNOWN;
			pPQ.head.graphNode = pPQ.lastInHeap.graphNode;
			PQNode tmp = pPQ.head.graphNode.nodeInPQ;
			pPQ.head.graphNode.nodeInPQ = pPQ.lastInHeap.graphNode.nodeInPQ;
			pPQ.lastInHeap.graphNode.nodeInPQ = tmp;
			if(pPQ.lastInHeap == pPQ.lastInHeap.parent.left)
			{
				pPQ.lastInHeap.parent.left = null;
			}
			else
			{
				pPQ.lastInHeap.parent.right = null;
			}
			pPQ.lastInHeap.parent = null;
			pPQ.lastInHeap.prev.next = null;
			pPQ.lastInHeap = pPQ.lastInHeap.prev;
			heapifyDown(pPQ.head.graphNode);
			return ret;
		}
	}


	@Override
	public void changePriority(GraphNode pData, int pNewPriority)
	{
		int oldPriority = pData.dist;
		pData.dist = pNewPriority;
		if(oldPriority > pNewPriority)
		{
			heapifyUp(pData);
		}
		else if(oldPriority < pNewPriority)
		{
			heapifyDown(pData);
		}
	}


	@Override
	public void displayPath(GraphNode v)
	{
		System.out.print("The shortest path to "+v.name+" is: ");
		GraphNode tmp = v;
		while(tmp.path != null){
			System.out.print(tmp.path.name+" ");
			tmp = tmp.path;
		}
		System.out.println("and its length is "+v.dist);
		
	}
	
	public void heapifyUp(GraphNode pNode)
	{
		try
		{
			if(pNode.nodeInPQ.parent != null){
				if(pNode.dist < pNode.nodeInPQ.parent.graphNode.dist)
				{
					pNode.nodeInPQ.graphNode = pNode.nodeInPQ.parent.graphNode;
					pNode.nodeInPQ.parent.graphNode = pNode;
					PQNode tmp = pNode.nodeInPQ;
					pNode.nodeInPQ = pNode.nodeInPQ.parent;
					pNode.nodeInPQ.parent = tmp;
				}
			}
		}
		catch(NullPointerException e){}
	}
	
	public void heapifyDown(GraphNode pNode)
	{
		try
		{
			if(pNode.nodeInPQ.left != null){
				if(pNode.dist > pNode.nodeInPQ.left.graphNode.dist)
				{
					pNode.nodeInPQ.graphNode = pNode.nodeInPQ.left.graphNode;
					pNode.nodeInPQ.left.graphNode = pNode;
					PQNode tmp = pNode.nodeInPQ;
					pNode.nodeInPQ = pNode.nodeInPQ.left;
					pNode.nodeInPQ.left = tmp;
				}
			}
			if(pNode.nodeInPQ.right != null){
				if(pNode.dist > pNode.nodeInPQ.right.graphNode.dist)
				{
					pNode.nodeInPQ.graphNode = pNode.nodeInPQ.right.graphNode;
					pNode.nodeInPQ.right.graphNode = pNode;
					PQNode tmp = pNode.nodeInPQ;
					pNode.nodeInPQ = pNode.nodeInPQ.right;
					pNode.nodeInPQ.right = tmp;
				}
			}
		}
		catch(NullPointerException e){}
	}
}
