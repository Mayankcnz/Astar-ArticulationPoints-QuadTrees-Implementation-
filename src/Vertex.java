import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.sun.glass.ui.Size;

public class Vertex {
	
	private int id;
	private List<Segment> inGoing;
	private List<Segment> outGoing;
	public List<Segment> allSegs;
	private Location location;
	private double default_size = 4;
	private double radius = default_size/2;
	private double x;
	private double y;
	private Color color = Color.BLACK;
	private boolean isVisited;
	private Vertex previous;
	private double g, h, f; // g = distance from source
	private int count = Integer.MAX_VALUE;
	private int reachBack;
	private Queue<Vertex> childrens;
                            // h is heuristic of destination
						
	public Vertex(int id, Location location, RoadMap m, Color color) {
		this.outGoing = new ArrayList<>();
		this.inGoing = new ArrayList<>();
		this.id = id;
		this.location = location;
		this.color = color;
		this.allSegs = new ArrayList<>();
	}
	
	public void addChildren(Vertex child) {
		this.childrens.add(child);
	}
	
	public void children(LinkedList<Vertex> list) {
		this.childrens = list;
	}
	
	public Queue<Vertex> getChildrens(){
		return this.childrens;
	}

	public Location getLocation() {
		return this.location;
	}
	
	public void addOutgoing(Segment seg) {
		this.outGoing.add(seg);
	}
	
	public void addinGoing(Segment seg) {
		this.inGoing.add(seg);
	}
	
	public void removeChildren(Vertex children) {
		this.childrens.remove(children);
	}
	
	public Vertex getChildren() {
		return childrens.poll();
	}
	
	
	public Segment getBetween(Vertex to) {
		for(Segment s : this.allSegs) {
			for(Segment s2 : to.allSegs) {
				if(s.equals(s2)) {
					return s;
				}
			}
		}
		
		return null;
	}
	
	public int getCount() {
		
		return this.count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	
	public void addSeg(Segment s) {
		this.allSegs.add(s);
	}
	
	public void draw(Graphics g, Location origin, double scale) {
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(this.color);//new Color(153, 102, 51)
		
		int size = 2;
		
		if(color != Color.red && color != Color.BLUE && color != Color.BLACK) {
		size = (int) (scale*0.1);
		if(size > 2)size = 2;
		}else {
			size = 10;
		}
		
		if(color == Color.MAGENTA) {
			size = 10;
			size = (int) (scale*0.1);
			if(size > 7)size = 7;
		}

		
		Point p = location.asPoint(origin,scale);
		this.x = p.x;
		this.y = p.y;
		g2.fillOval(p.x - (int)radius, p.y-(int)radius, size, size);
	}
	
	public boolean on(double x, double y) {
		
		double centerX = this.x + (radius/2.0);
		double centerY = this.y + (radius/2.0);
        if(Math.hypot(x - centerX, y - centerY) < radius) {
            return true;

        }
        return false;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getRoadID() {
		return this.id;
	}
	
	public void setVisited(boolean bol) {
		
		this.isVisited = bol;
	}
	
	public boolean getVisited() {
		return this.isVisited;
	}
	
	public void setReachBack(int reachBack) {
		this.reachBack = reachBack;
	}
	
	public int getReachBack() {
		return this.reachBack;
	}
	
	public ArrayList<Vertex> getNeighbours() {
		ArrayList<Vertex> neighbours = new ArrayList<>();
		for (Segment s:getAllOutgoing()){
			neighbours.add(s.getNeighbours(this));
		}
		return neighbours;
	}
	
	public void setPrevious(Vertex pre) {
		this.previous = pre;
	}
	
	public Vertex getPrevious() {
		return this.previous;
	}
	
	public double estimate(Vertex destination, String by) {
		if(by.equals("time")) {
			double distance = location.distance(destination.getLocation());
            return distance/110.0;
		}else {
			return this.location.distance(destination.getLocation());
		}
	}
	
	public void setG(double edgeWeight) {
		this.g = edgeWeight;
	}
	
	public double getG() {
		return this.g;
	}
	
	public double getF() {
		return this.f;
	}
	
	public double getH() {
		return this.h;
	}
	
	
	public String toString() {
		return "("+id+" : "+location.toString()+")";
	}
	
	public List<Segment> getAllOutgoing(){
		return this.outGoing;
	}
	
	public List<Segment> getIncoming(){
		return this.inGoing;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Set<String> getAllSegs(){
		Set<String> set = new HashSet<>();
		outGoing.forEach(s -> set.add(s.getRoad().getRoadName()));
		return set;
	}
	
}
