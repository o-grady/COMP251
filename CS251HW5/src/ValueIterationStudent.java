
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import HW5.*;
import HW5.LakeEnvir.Action;
import HW5.LakeEnvir.RandomState;
import HW5.LakeEnvir.RandomState.Outcome;
import HW5.LakeEnvir.State;

public class ValueIterationStudent implements ValueIterationInterface{
	
	// used to create the Graphical Interface
	static JFrame frame;
	
	/**
	 * Main function: feel free to change
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String args[]) {
		//the environment we create with default parameters
		LakeEnvir.LakeParameters P = new LakeEnvir.LakeParameters();
		
		//create the Graphical Interface
		frame = new JFrame();
		frame.setTitle("DrawRect");
		frame.setSize(P.GIScale * (P.horizontalLength + 2), 
				P.GIScale * (P.verticalLength + 2));
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		Container contentPane = frame.getContentPane();
		LakeEnvir d = new LakeEnvir(P);
		contentPane.add(d);
		frame.show();
		
		//simulate the algorithms
		for(Algotype algo : Algotype.values()) {
			simulateAlgo(algo, d);
		}
	}
	
	
	/**
	 * the types of algorithms to be tested
	 */
	public enum Algotype {SYNCH, ASYNCH, ASYNCH_PUSH };
	
	/**
	 * Method used to simulate the policy resulting from the value function
	 * at each iteration of "value iteration". This is done on different 
	 * types of algorithms (Feel free to change)
	 * @param pAlgo : type of algorithm to simulate
	 * @param pLake : environment to run on
	 */
	private static void simulateAlgo(Algotype pAlgo, LakeEnvir pLake) {
		
		ValueFunction V = new ValueFunction(pLake);
		ValueIterationInterface vi = new ValueIterationStudent();
		for (int i = 0; i < 20; i++) {
			Policy pi = vi.getPolicy(V);
			pLake.repaint();
			pLake.drawPolicy(pi);
			System.out.println("Done drawing policy!");
			pLake.simulatePolicy(pLake.new State(pLake.horizLength() - 1, pLake.vertLength() - 1), pi, 30);
			System.out.println("Done simulating policy!"); 
			try {
				java.lang.Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			switch (pAlgo) {
			case SYNCH:
				vi.synchValueIteration(V);
				break;
			case ASYNCH:
				vi.asynchValueIteration(V);
				break;
			case ASYNCH_PUSH:
				vi.pushValueIteration(V);
				break;
			}
		}
	}

	@Override
	public void synchValueIteration(ValueFunction V)
	{
		ValueFunction vPrev = new ValueFunction(V.lake);
		for(int i = 0 ; i < V.lake.horizLength() ; i++)
		{
			for(int j = 0 ; j < V.lake.vertLength() ; j++)
			{
				//Save previous state
				State s = vPrev.lake.new State(i,j);
				State t = V.lake.new State(i,j);
				vPrev.updateValue(s,V.getValue(t));
			}
		}
		for(int i = 0 ; i < V.lake.horizLength() ; i++)
		{
			for(int j = 0 ; j < V.lake.vertLength() ; j++)
			{
				State s = V.lake.new State(i,j);
				double best = (double)Integer.MIN_VALUE;
				for(Action a : LakeEnvir.Action.values())
				{
					double current = 0;
					double totalweight = 0;
					for(Outcome x : V.lake.getNextState(s, a).getPossibleOutcomes())
					{
						totalweight = totalweight + (double)x.weight;
					}
					for(Outcome x : V.lake.getNextState(s, a).getPossibleOutcomes())
					{
						State previous = vPrev.lake.new State(x.state.hPos(),x.state.vPos());
						current = current + (((double)x.weight/totalweight) * ((double)V.lake.getReward(s, a, x.state) + vPrev.getValue(previous) * GAMMA));
					}
					if(best < current)
					{
						best = current;
					}
				}
				V.updateValue(s, best);
			}
		}
		
	}

	@Override
	public void asynchValueIteration(ValueFunction V)
	{
		for(int i = 0 ; i < V.lake.horizLength() ; i++)
		{
			for(int j = 0 ; j < V.lake.vertLength() ; j++)
			{
				State s = V.lake.new State(i,j);
				double best = (double)Integer.MIN_VALUE;
				for(Action a : LakeEnvir.Action.values())
				{
					double current = 0;
					double totalweight = 0;
					for(Outcome x : V.lake.getNextState(s, a).getPossibleOutcomes())
					{
						totalweight = totalweight + (double)x.weight;
					}
					for(Outcome x : V.lake.getNextState(s, a).getPossibleOutcomes())
					{
						current = current + (((double)x.weight/totalweight) * ((double)V.lake.getReward(s, a, x.state) + V.getValue(x.state) * GAMMA));
					}
					if(best < current)
					{
						best = current;
					}
				}
				V.updateValue(s, best);
			}
		}
		
	}

	@Override
	public void pushValueIteration(ValueFunction V)
	{
	
		
	}

	@Override
	public Policy getPolicy(final ValueFunction V)
	{
		final LakeEnvir lake = V.lake;
		class aPolicy implements Policy
		{
			public Action getAction(State s)
			{
				double max = (double)Integer.MIN_VALUE;
				Action ret = null;
				for(Action a : LakeEnvir.Action.values())
				{
					double current = 0;
					for(Outcome x : lake.getNextState(s, a).getPossibleOutcomes())
					{
						current = current + (double)lake.getReward(s, a, x.state)+GAMMA*V.getValue(x.state);
					}
					if(current > max)
					{
						max =  current;
						ret = a;
					}	
				}
				return ret;
			}
		}
		return new aPolicy();
	}
}