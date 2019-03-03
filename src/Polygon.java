import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

public class Polygon{
	
	private String hexCode;
	private List<Location> data;
	private int idxLevel;
	private int endLevel;
	
	
	public Polygon(String hexCode, List<Location> data, int idxLevel, int endLevel) {
		
		this.hexCode = hexCode;
		this.data = data;
		this.idxLevel = idxLevel;
		this.endLevel = endLevel;
	}
	
	public void draw(Graphics g, Location origin, double scale) {
	
	
		Graphics2D g2 = (Graphics2D) g;
		setColor(g);
		
		int x[] = new int[data.size()];
		int y[] = new int[data.size()];
		
		int i;
		for(i = 0; i < data.size(); i++) {
			Point p1 = data.get(i).asPoint(origin, scale);
			x[i] = p1.x;
			y[i] = p1.y;
			
		}

		g2.fillPolygon(x,y,i);
		
		
	}
	
	private void setColor(Graphics g) {
		
		
		switch(hexCode){
		
		case "0x3c":
			g.setColor(new Color(255, 255, 230));
			break;
			
		case "0xa":
			g.setColor(new Color(200, 187, 163));
			break;
			
		case "0xe":
			g.setColor(new Color(220,220,210));
			break;
			
			
		case "0x3e":
			g.setColor(Color.gray);
			break;
		
		case "0x13":
			g.setColor(new Color(200, 187, 163));
			break;
			
			
		case "0x28":
			g.setColor(new Color(15,64,155));
			break;
			
		case "0x7":
			g.setColor(new Color(200, 187, 163));
			break;
			
		case "0x8":
			g.setColor(new Color(237,221,198));
			break;
			
		case "0x17":
			g.setColor(new Color(34,139,34));
			break;
			
		case "0x19":
			g.setColor(new Color(34,139,34));
			break;
			
		case "0x1":
			g.setColor(new Color(255, 229, 204));
			break;
			
		case "0x48":
			g.setColor(Color.BLUE);
			break;
			//0x1a
			
		case "0x1a":
			g.setColor(new Color(255, 229, 204));
			break;
			
			
		case "0xb":
			g.setColor(new Color(34,139,34));
			break;
			
		case "0x18":
			g.setColor(new Color(220, 223, 170));
			break;
			
		case "0x50":
			g.setColor(new Color(204,229,163));
			break;
		
		case "0x2":
			g.setColor( new Color(234,234,234));
			break;
			
		default:
			g.setColor(new Color(45, 146, 237));
		}
}
}
