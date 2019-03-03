import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuadTree {
	
	
	private static final int NODE_CAPACITY = 4;
	private List<QuadTree> quads;
	//private Map<Point, Vertex>points;
	private Map<Point, Vertex>points;
	private Rectangle boundingBox;
	private boolean divided;
	
	public QuadTree(Rectangle boundingBox) {
		
		this.boundingBox = boundingBox;
		points =  new HashMap<>();
		quads = new ArrayList<>(NODE_CAPACITY);
	}
	
	public int size() {
		return points.size();
	}

	public void split() {
		int x = (int) this.boundingBox.getX();
		int y = (int) this.boundingBox.getY();
		int width =  (int) this.boundingBox.getWidth();
		int height = (int) this.boundingBox.getHeight();
		
		int halfW = width / 2;
        int halfH = height / 2;
        
        Rectangle nW = new Rectangle(x, y,halfW, halfH);
        Rectangle nE = new Rectangle(x+halfW,y,halfW,halfH );
        Rectangle sW = new Rectangle(x, y + halfH ,halfW,halfH );
        Rectangle sE = new Rectangle(x + halfW, y + halfH ,halfW,halfH);
        
        this.quads.add(new QuadTree(nW)); //northWest
        this.quads.add(new QuadTree(nE)); // northEast
        this.quads.add(new QuadTree(sW)); // southWest
        this.quads.add(new QuadTree(sE)); // southEast
        
        /**
         *    []
         *    /\
         *[data][data] // transfer all the current data to new leaf nodes
         */
        
        for(Point p : points.keySet()) {
        	for(QuadTree quad : quads) { // check does any of the quad can contain the current point, if so tranfer it to it
        		if(quad.getBoundingBox().contains(p)) {
        			if(quad.insert(p, points.get(p)))break;
        		}
        	}
        }
        
        this.points.clear();
		
	}
	public boolean insert(Point point, Vertex vertex) {
		
		if(!boundingBox.contains(point)) {
			return false;
		}
		if(!this.divided && points.size() < NODE_CAPACITY) {
			points.put(point, vertex);
			return true;
		}else {
			if(!this.divided) {
			this.split();
			this.divided = true;
			}
		}
		
		if(this.quads.get(0).insert(point, vertex)) {return true;}
		if(this.quads.get(1).insert(point, vertex)) {return true;}
		if(this.quads.get(2).insert(point, vertex)) {return true;}
		if(this.quads.get(3).insert(point, vertex)) {return true;}
		
		return false;
	}
	
	public ArrayList<QuadTree> getChildren(){
		return new ArrayList<>(quads);
	}
	
	public List<QuadTree> getInRange(Point p, int radius){
		
		
		if(!divided)return null;
		
		List<QuadTree> list = new ArrayList<>();
		
		for(QuadTree t : quads) {
			if(t.intersects(t.boundingBox, p , radius)) {
				list.add(t);
			}
		}
		
		return list;
		
	}
	
	private boolean intersects(Rectangle rect, Point p, int radius) {
		
		// need to find the center point
		int centerX = p.x;
		int centerY = p.y;
		
		return rect.intersects(new Rectangle(centerX-radius,centerY-radius, radius*2, radius*2));
	}
	
	
	public boolean isDivided() {
		
		return this.divided;
	}
	
	public List<Point> getPoints(){
		return new ArrayList<>(points.keySet());
	}
	
	public Rectangle getBoundingBox() {
		return this.boundingBox;
	}
	
	public Vertex getNode(Point p) {
	   return points.get(p);
	}
	
}
