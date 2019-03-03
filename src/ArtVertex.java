import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ArtVertex {
	
	
	private Vertex current;
	private Vertex parent;
	private int count;
	private Queue<Vertex> childrens;

	
	public ArtVertex(Vertex firstNode, int count, Vertex parent) {
		this.current = firstNode;
		this.count = count;
		this.parent = parent;
		
	}
	
	public void removeChildren(Vertex children) {
		this.childrens.remove(children);
	}
	
	
	public int getCount() {
		return this.count;
	}
	
	public Vertex getCurrent() {
		return this.current;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	
	public void addChildren(Vertex child) {
		this.childrens.add(child);
	}
	
	public int getChildrens(){
		return this.childrens.size();
	}
	
	public Vertex getChildren() {
		return childrens.peek();
	}
	
	public void children(LinkedList<Vertex> list) {
		this.childrens = list;
	}
	public Vertex getParent() {
		
		return this.parent;
	}
	

	public int getParentReachBack() {
		return parent.getReachBack();
	}

}
