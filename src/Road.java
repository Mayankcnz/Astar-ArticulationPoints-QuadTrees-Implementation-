import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Road {
	
	private Color roadColor;
	private List<Segment> segments;
	private int roadID;
	private int type;
	private String roadName;
	private String city;
	private Integer[] speedLimit;
	private int roadclass;
	private int oneWay;
	private int speed;
	private int notforcar;
	private int notforpedestrians;
	private int notforbicycle;
	private boolean highlited;
	
	public Road(int roadID, int type, String label, String city, int oneway, int speed, int roadclass, int notforcar, int notforpede, int notforbicycle) {
		
		initialize();
		this.roadColor = Color.BLACK;
		this.roadID = roadID;
		this.type = type;
		this.roadName = label;
		this.city = city;
		this.oneWay = oneway;
		this.speed = speed;
		this.roadclass = roadclass;
		this.notforcar = notforcar;
		this.notforpedestrians = notforpede;
		this.notforbicycle = notforbicycle;
	}
	
	private void initialize() {
		
		segments = new ArrayList<>();
		speedLimit = new Integer[] {5,20,40,60,80,100,100,100};
		
	}
	
	public int getSpeed() {
		
		
		return speedLimit[speed];
	}
	
	public int getRoadClass() {
		return this.roadclass;
	}
	
	public void addSegment(Segment seg) {
		this.segments.add(seg);
	}
	
	public int getRoadWay() {
		
		return oneWay;
	}
	
	public String getRoadName() {
		return this.roadName;
	}
	
	public void highlightRoad() {
		
		this.highlited = true;
		this.roadColor = Color.RED;
	}
	
	public Color getColor() {
		return this.roadColor;
	}
	
	public boolean isHighlighted() {
		return this.highlited;
	}
	
	public void turnHighlightOff() {
		this.highlited = false;
		this.roadColor = Color.BLACK;
	}


}
 