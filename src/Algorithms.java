import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Algorithms {
	
	private RoadMap r;
	
	public Algorithms(RoadMap map) {
		this.r = map;
	}
	
	public Set<Vertex> findAllArticulationPoints() {
		

		Set<Vertex> articulationPoints = new HashSet<>();
		// also need to go over the possible nodes

		// each neighbour of the roor
		List<Vertex> nodes = new ArrayList<>(this.r.getVertices()); 
		while(!nodes.isEmpty()) {
		 	Vertex node = nodes.remove(0);
		 	node.setCount(0);
			int numSubTrees = 0;
			for (Vertex v : node.getNeighbours()) {
				if (v.getCount() == Integer.MAX_VALUE) {
					iterArtPts(v, 1, node, articulationPoints, nodes);
					numSubTrees++;
				}
			}
			if (numSubTrees > 1) {
				node.setColor(Color.MAGENTA);
				articulationPoints.add(node);
			}
		}

		
		return articulationPoints;
	}

	public void iterArtPts(Vertex neigh, int count, Vertex root, Set<Vertex> aps, List<Vertex> nodes) {

		Stack<ArtVertex> stack = new Stack<>();
		stack.push(new ArtVertex(neigh, count, root));

		while (!stack.isEmpty()) {
			//System.out.println(stack.size());
			ArtVertex v = stack.peek();
			Vertex node = v.getCurrent();
			Vertex parent = v.getParent();
			int counter = v.getCount();

			if (node.getCount() == Integer.MAX_VALUE) {
				node.setCount(counter);
				node.setReachBack(counter);
				node.children(new LinkedList<Vertex>());
				for (Vertex neighbour : node.getNeighbours()) {
					if (!neighbour.equals(parent)) { // cant be equals to the parent of this node
						node.addChildren(neighbour); // add the children as vertex, all initially intialized with Integer.Positive_Infinity
					}
				}
			} else if (!node.getChildrens().isEmpty()) {
				Vertex child = node.getChildren();
				//node.removeChildren(child); // to remove so that we do not go in infinite loop
				if (child.getCount() < Integer.MAX_VALUE) { // been visited before, yes we visited A before before
					node.setReachBack(Math.min(node.getReachBack(), child.getCount()));// update the count of current node,																// with the count of the children
				} else
					stack.push(new ArtVertex(child, counter + 1, node));
			} else {
				if (!node.equals(neigh)) {
					parent.setReachBack(Math.min(parent.getReachBack(), node.getReachBack()));
					if (node.getReachBack() >= parent.getCount()) { // if this parent reachback greater than the parent count, then yes we found an ap
						parent.setColor(Color.MAGENTA);	
						aps.add(parent);// then the parent is an ap
					}
				}
				stack.remove(v);
			}
		}

	}

}
