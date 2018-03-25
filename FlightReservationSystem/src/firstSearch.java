//package first;
import java.util.*;

public class firstSearch {
	  public static void main(String[] args) {
		  List<List<Integer>> exm = new ArrayList<List<Integer>>();
		  String[] inputLines = {"2 1", "3 1","1 4","2 5","2 3","3 5","4 5"};
		  
		  for(String line : inputLines){
			  List<Integer> row = new ArrayList<Integer>();
			  Scanner s = new Scanner(line);
			  while (s.hasNextInt())
				  row.add(s.nextInt());
			  
			 exm.add(row);
			System.out.println(row);
		  }
		  
		  
		  firstSearch transfershow = new firstSearch();
		  Set<Integer> transans = transfershow.search(exm.size(),exm,3);
		  for(int every: transans )
	        {             
	            System.out.println(every);            
	        } 
		  
		  
	  }

	  public Set<Integer> search(int n, List<List<Integer>> simplelist, int dep){
		  Map<Integer, Set<Integer>> graph = initializeGraph(n, simplelist);
		  
		  Queue<Integer> queue = new LinkedList<>();
		  Set<Integer> hash = new HashSet<>();
		  
		  queue.offer(dep);
		  hash.add(dep);
		  
		  while(!queue.isEmpty()){
			  int node = queue.poll();
			  for(Integer neighbor: graph.get(node)){
				  if(hash.contains(neighbor)){
					  continue;
				  }
				  hash.add(neighbor);
				  queue.offer(neighbor);
			  }
		  }
		  return hash;
			  
	  }
		

	  
	  
	  private Map<Integer, Set<Integer>> initializeGraph(int n, List<List<Integer>> simplelist) {
	        Map<Integer, Set<Integer>> graph = new HashMap<>();
	        for (int i = 0; i < n; i++) {
	            graph.put(i, new HashSet<Integer>());
	        }
	        
	        for (int i = 0; i < simplelist.size(); i++) {
	            int u = simplelist.get(i).get(0);
	            int v = simplelist.get(i).get(1);
	            graph.get(v).add(u);
	            graph.get(u).add(v);
	        }
	        
	        return graph;
	  }
	  
}



