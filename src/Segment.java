import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Segment {

	private Road road;
	private int roadID;
	private Vertex from;
	private Vertex to;
	private int cost;
	private double length;
	private boolean highlight;
	private boolean isRestricted;
	private List<Location> coords;
	
	
	public Segment(Road road, int roadID, Vertex node1, Vertex node2, double length, List<Location>coords) {
		this.road = road;
		this.roadID = roadID;
		this.from = node1;
		this.to = node2;
		this.length = length;
		this.coords = new ArrayList<>(coords);
	}
	
	public void setRestricted(boolean bol) {
		this.isRestricted = bol;
	}
	
	public boolean isRestricted() {
		return this.isRestricted;
	}
	
	public void draw(Graphics g, Location origin, double scale, String filteredItem) {

		Graphics2D g2 = (Graphics2D) g;
		int stroke = 1;
		if (road.isHighlighted() || this.highlight) {
			stroke = 3;
			g2.setColor(road.getColor());
		} else if (road.getRoadName().contains("rd")) {
			g2.setColor(new Color(149, 192, 197));
			stroke = 3;
		} else {
			stroke = 1;
			g2.setColor(new Color(149, 192, 197));
		}
		if (road.getRoadName().contains("motorway") || road.getRoadName().contains("highway")) {
			stroke = 10;
			g2.setColor(new Color(229, 167, 15));
		}
		
		if(this.highlight) {
			g2.setColor(Color.MAGENTA);
		}

		g2.setStroke(new BasicStroke(stroke));
		for (int i = 0; i < coords.size() - 1; i++) {
			Point p1 = coords.get(i).asPoint(origin, scale);
			Point p2 = coords.get(i + 1).asPoint(origin, scale);
			g2.drawLine(p1.x, p1.y, p2.x, p2.y);
		}

	}
	
	
	public boolean isOneWay() {
		
		return this.road.getRoadWay() == 1;
	}
	
	public Vertex getNeighbours(Vertex v) {
		if (this.to.equals(v)){
			return this.from;
		}
		else{
			return this.to;
		}
	}
	
	public double getSpeedLimit() {
		
		return this.road.getSpeed();
	}
	
	public int getRoadID() {
		return this.roadID;
	}
	
	public Road getRoad() {
		return this.road;
	}
	
	public String getName() {
		return this.road.getRoadName();
	}
	
	public Vertex getTo() {
		return this.to;
	}
	
	public Vertex getNode(Vertex to) {
		if(this.to.equals(to)) {
			return this.from;
		}else {
			return this.to;
		}
	}
	
	public void setHighlight(boolean bol) {
		this.highlight = bol;
	}
	
	public Vertex getFrom() {
		return this.from;
	}
	
	public double getCost(String by) {
		
		if(by.equals("time")) {
			return (length/((double) (road.getSpeed() + (road.getRoadClass()))));
			//return ((length/(double) road.getSpeed())*60.00);
		}else {
			return this.length; // just simply return the length 
		}
	}
	
}
