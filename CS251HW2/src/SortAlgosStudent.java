import javax.swing.*;
import java.awt.Color;
import java.util.Random;
import HW2.*;
import org.math.plot.*;


/** Class to test various sorts. */
public class SortAlgosStudent extends SortAlgosAbstract{
    
	
	
	public void heapSort(int[] pToSort) {
		HeapNode root = new HeapNode(pToSort[0]);
        buildHeap(root, pToSort, 0);

        for (int i = pToSort.length - 1; i >= 1; i--) {
            pToSort[i] = root.value; 
            HeapNode leaf = getLeaf(root);
            root.value = leaf.value;
            heapify(root);
        }
        pToSort[0] = root.value;
	}
	
	
	public void inPlaceHeapSort(int[] pToSort) {
		int lastHeapElement = pToSort.length;
		inPlaceBuildHeap(pToSort);
        for (int i = pToSort.length - 1; i >= 1; i--) {
            int temp = pToSort[i];
            pToSort[i] = pToSort[0];
            pToSort[0] = temp;
            inPlaceHeapify(0, pToSort, --lastHeapElement);
        }
	}
	

    
    public static void main(String[] args) {        
    	SortAlgosStudent tester = new SortAlgosStudent();  
    	boolean toTest = true;
    	
    	/** UNCOMMENT THE FOLLOWING IF YOU DON'T WANT TO RUN YOUR OWN TESTS*/
    	//toTest = false;
    	    	
    	/** UNCOMMENT THE FOLLOWING TO GENERATE REQUIRED PNGs */
    	//tester.generateRequiredPlots();
        
    	if(!toTest) return;
    	
        /** FEEL FREE TO MODIFY THE FOLLOWING FOR YOUR OWN TESTING PURPOSES */
    	
    	/**  User-defined constants */
    	/****************************/
    	Random r = new Random(tester.SEED);
        final int minSize = 1000;
        final int sizeIncrement = 1000;
        final int maxSize = 15000;
        final int averageOverNumRuns = 10;  
        final int maxInt = 100000;
        /****************************/
        
        
        int a[]; //array holding values
        
        /** Used to plot all algos */
        Plot2DPanel plotHeapAlgos = new Plot2DPanel("SOUTH");
        
        
        for(AlgoType sortType : AlgoType.values()) {        	
        	System.out.println("Working on " + sortType.name() + "...");
        	/********/
        	double[] x = new double[(maxSize - minSize) / sizeIncrement + 1];
        	x[0] = minSize;
        	for (int i = 1; i < x.length; i++) {
    			x[i] = x[i-1] + sizeIncrement;
    		}            
        	double[] mins = new double[x.length];
        	double[] maxs = new double[x.length];
        	double[] avgs = new double[x.length];
        	
        	for (int i = 0; i < x.length; i++) {
        		long total = 0, min = Integer.MAX_VALUE, max = 0;        		
				for (int j = 0; j < averageOverNumRuns; j++) {				
					// initialize array to random values
					a = new int[(int) x[i]];
			        for (int k=0;k<a.length;k++)
			            a[k] = r.nextInt(maxInt);
					
					long time = System.currentTimeMillis();
					// select whichever sort we are doing					
					switch(sortType) {
					case SELECTION: tester.selectionSort(a);break;
					case INSERTION: tester.insertionSort(a); break;
					case HEAP_ARRAY: tester.inPlaceHeapSort(a); break;
					case HEAP: tester.heapSort(a); break;
					}
					time = System.currentTimeMillis() - time;	
					total += time;
					min = Math.min(time, min);
					max = Math.max(time, max);
				} // for j
				mins[i] =  min ;
				maxs[i] =  max ;
				avgs[i] = ((double) total) / averageOverNumRuns;	
			} //for i
        	
        	Color c = Color.BLACK;
        	switch(sortType) {
        	case SELECTION: c = Color.BLACK;break;
        	case INSERTION: c = Color.RED; break;
        	case HEAP_ARRAY: c = Color.BLUE; break;
        	case HEAP: c = Color.GREEN; break;
        	}
        	  
        	if(sortType == AlgoType.HEAP || sortType == AlgoType.HEAP_ARRAY) {
        		plotHeapAlgos.addLinePlot(sortType.name(), c, x, avgs);
        	}
        	
        	/** Used to plot individual algo */
        	Plot2DPanel plotIndividual = new Plot2DPanel("SOUTH");        	               	
        	plotIndividual.addLinePlot(sortType.name() + " minimum", Color.RED, x, mins);
        	plotIndividual.addLinePlot(sortType.name() + " maximum", Color.BLACK, x, maxs);
        	plotIndividual.addLinePlot(sortType.name() + " average", Color.GREEN, x, avgs);
        	JFrame frame = new JFrame("Plot for " + sortType.name());
        	frame.setSize(600, 600);
            frame.setContentPane(plotIndividual);
            frame.setVisible(true);
        }

        // put the PlotPanel in a JFrame like a JPanel
        JFrame frame = new JFrame("Plot for heap implementations");
        frame.setSize(600, 600);
        frame.setContentPane(plotHeapAlgos);
        frame.setVisible(true);   
        
        
    }   
}
