import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import HW6.*;
import HW6.Residual.Edge;
import HW6.Residual.Path;

public class FordFulkersonStudent extends FordFulkAbstract{

	
	public static void main(String[] args) {		
		try {
			String todo = "myGraph.txt";
			OriginalGraph G = new OriginalGraph(todo);
			FordFulkersonStudent ff = new FordFulkersonStudent();
			double max_flow = ff.maxFlow(G, 0, G.numS()-1);
			System.out.println("The max flow on the graph " +  todo + " is " + max_flow);
			System.out.println("If the answer is correct make sure you try it on some other graph" +
					". If you don't know how to input another graph, this course has a Discussion Board which you should use");
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * Computes the maximum flow between two states of a given graph, 
	 * using the Fold-Fulkerson algorithm
	 * @param G : the graph over which the algorithm is to be run
	 * @param s : index of the state of origin of the flow
	 * @param t : index of the state of destination of the flow
	 * @return the maximum flow of the network
	 */
	public int maxFlow(OriginalGraph G, int s, int t) {
		Residual residual = new Residual(G);
		Residual.Path p = getAugmentingPath(residual, s, t);
		while(p != null) {
			System.out.println(p);
			augment(residual, p);			
			System.out.println(residual);
			p = getAugmentingPath(residual, s, t);
		}
		return residual.extractFlow();
	}

	@Override
	public void augment(Residual pResidual, Path pPath)
	{
		for(Edge e : pPath.pathElements())
		{
			e.residual -= pPath.min(); //Decrease capacity by min value in the path
			if(e.residual == 0)
			{
				pResidual.graph.get(e.origin).remove(e); // remove an edge if it has no capacity
			}
			if(e.opposite == null)
			{
				//crease an oppoite edge if it does not exist
				Edge opposite = pResidual.new Edge();
				opposite.destination = e.origin;
				opposite.origin = e.destination;
				opposite.opposite = e;
				opposite.residual = pPath.min();
				pResidual.graph.get(e.destination).add(opposite);
			}
			else
			{
				e.opposite.residual += pPath.min();//increase capacity of opposite edge
			}
		}
		
	}

	@Override
	public Path getAugmentingPath(Residual G, int s, int t)
	{
		Path ret = G.new Path();
		LinkedList<Edge> current = G.graph.get(s);
		Queue<LinkedList<Edge>> q = new ConcurrentLinkedQueue<LinkedList<Edge>>();
		int[] parents = new int[G.graph.size()]; // used for storing the previous node in BFS
		boolean[] visited = new boolean[G.graph.size()]; // marks if a node has been visited
		parents[s] = -1; //used as a stopping value when building path
		q.add(current);
		while(!q.isEmpty())
		{ // BFS
			current = q.poll();
			if(G.graph.indexOf(current) == t)//if path to T is found, creates and returns path
			{
				int currentPosition = t;
				while(parents[currentPosition] != -1)
				{
					Edge toAdd = null;
					for(Edge e : G.graph.get(parents[currentPosition]))
					{
						if(e.destination == currentPosition)
						{
							toAdd = e;
						}
					}
					ret.addFirst(toAdd);
					currentPosition = parents[currentPosition];
				}
				return ret;
			}
			
			visited[G.graph.indexOf(current)] = true;
			
			for(Edge e : current)
			{
				if(!visited[e.destination] && e.residual != 0)
				{
					q.add(G.graph.get(e.destination));
					parents[e.destination] = e.origin;
				}
			}
		}
		return null;
	}
}
