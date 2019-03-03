
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.crypto.AEADBadTagException;
import javax.sound.midi.Synthesizer;
import javax.swing.DefaultComboBoxModel;

public class RoadMap extends GUI {

	private int lastMouseX, lastMouseY;
	private static final double ZOOM_IN = .8, ZOOM_OUT = 1.2;
	private Location origin = Location.newFromLatLon(-36.847622, 174.763444);
	private double scale = 60;
	private List<Segment> allSegments;
	private java.util.Map<Integer, Vertex> nodes; // roadID, node
	private java.util.Map<Integer, Road> allRoades; // roadid -> road
	private List<Road> filteredItems;
	private String filter;
	private TrieNode trieRoot;
	private List<Polygon> endLevel1;
	private List<Polygon> endLevel2;
	private List<Polygon> endLevel3;
	private Set<Vertex> articulationPoints;
	private QuadTree quadTree;
	private boolean makeNew, last;
	private int firstX;
	private Vertex selected, goal;
	private boolean mouseEvent, load_articulationPoints, clickEvent;
	private Location minLoc, maxLoc;
	private Algorithms algo;
	private List<Segment> highlightedPath;
	private List<Segment> inters;

	public RoadMap() {

		allSegments = new ArrayList<>();
		nodes = new HashMap<>();
		allRoades = new HashMap<>();
		trieRoot = new TrieNode();
		filteredItems = new ArrayList<>();
		endLevel1 = new ArrayList<>();
		endLevel2 = new ArrayList<>();
		endLevel3 = new ArrayList<>();
		highlightedPath = new ArrayList<>();
		inters = new ArrayList<>();

		this.quadTree = new QuadTree(new Rectangle(0, 0, (int) getDrawingAreaDimension().getWidth(),
				(int) getDrawingAreaDimension().getHeight()));
		
		this.algo = new Algorithms(this);
	}

	@Override
	protected void redraw(Graphics g) {
		

		if (makeNew) {
			this.quadTree = new QuadTree(new Rectangle(0, 0, (int) getDrawingAreaDimension().getWidth(),
					(int) getDrawingAreaDimension().getHeight()));
			for (Vertex v : nodes.values()) {
				quadTree.insert(v.getLocation().asPoint(origin, scale), v);
			}
		}

		if (UPDATE_ON_EVERY_CHARACTER && this.filter != null && this.filteredItems != null
				&& this.filteredItems.size() >= 1 && this.filter.length() >= 1) {
			j.showPopup();
		}

		if (j.getSelectedIndex() != -1 || this.mouseEvent) {
			j.hidePopup();
			UPDATE_ON_EVERY_CHARACTER = false;
			this.mouseEvent = false;
		} else {
			UPDATE_ON_EVERY_CHARACTER = true;
		}

		if (!nodes.isEmpty()) {
			findMinMax();
			g.setColor(new Color(249, 244, 195));
			Point min = minLoc.asPoint(origin, scale);
			Point max = maxLoc.asPoint(origin, scale);
			g.fillRect(min.x, min.y, max.x - min.x, max.y - min.y);
		}

		for (Polygon p : endLevel3)
			p.draw(g, origin, scale);

		for (Polygon p : endLevel2)
			p.draw(g, origin, scale);

		for (Polygon p : endLevel1)
			p.draw(g, origin, scale);

		for (Segment s : allSegments) // draw segments
			s.draw(g, origin, scale, this.filter);

		for (Vertex s : nodes.values()) // draw Vertex
			s.draw(g, origin, scale);

	}

	@Override
	protected void onClick(MouseEvent e) {
		
		
		if (e.getID() == 501) { // on press
			firstX = e.getX();
			Point p = new Point(e.getX(), e.getY());
			Location loc = Location.newFromPoint(p, this.origin, this.scale);
			this.lastMouseX = e.getX();
			this.lastMouseY = e.getY();

		} else if (e.getID() == 502) { // on release
			
			clearArticulationPoint();

			if (firstX == e.getX()) { // only find the closest node, if the user not panning
				Vertex closest = getClosestPoint(new Point(e.getX(), e.getY()), quadTree, 100); // get the closest
																								// vertex to the mouse
																								// released location
				if (closest != null) {
					getTextOutputArea().setText("");

					if (this.selected == null) {
						this.selected = closest;
						this.selected.setColor(Color.RED);
					} else if (this.goal == null) {
						this.goal = closest;
						this.goal.setColor(Color.BLUE);
					} else if (!last) {
						this.selected.setColor(null);
						this.selected = closest;
						this.selected.setColor(Color.RED);
						last = true;
					} else if (last) {
						this.goal.setColor(null);
						this.goal = closest;
						this.goal.setColor(Color.BLUE);
						last = false;
					}

					int roadID = closest.getRoadID();
					getTextOutputArea().append(roadID + "\n");
					List<String> segs = new ArrayList<>(closest.getAllSegs());
					getTextOutputArea().append("[");
					for (int i = 0; i < segs.size(); i++) {
						if (i == segs.size() - 1) {
							getTextOutputArea().append(segs.get(i) + "");
						} else {
							getTextOutputArea().append(segs.get(i) + ", ");
						}
					}
					getTextOutputArea().append("]");
				}
			}
		}

		this.mouseEvent = true;
	}
	
	private void clearArticulationPoint() {
		
		if(articulationPoints == null)return;
		
		for(Vertex v : this.articulationPoints) {
			v.setColor(null);
		}
		
		super.art.setBackground(null);
	}

	private void findMinMax() {

		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;

		for (Vertex v : nodes.values()) {
			Location loc = v.getLocation();
			Point p = loc.asPoint(origin, scale);
			if (p.getY() > maxY) {
				maxY = p.getY();
			} else if (p.getY() < minY) {
				minY = p.getY();
			}

			if (p.getX() > maxX) {
				maxX = p.getX();
			} else if (p.getX() < minX) {
				minX = p.getX();
			}
		}

		minLoc = Location.newFromPoint(new Point((int) minX, (int) minY), origin, scale);
		maxLoc = Location.newFromPoint(new Point((int) maxX, (int) maxY), origin, scale);

	}

	@Override
	protected void onSearch() {

		String search = getSearchBox().getText();

		if (this.filteredItems != null) {
			for (Road road : this.filteredItems) {
				road.turnHighlightOff();
			}
		}

		if (search.length() < 1) {
			this.mouseEvent = true;
			return; // otherwise trie would extract all the roads
		}

		this.filter = search;

		filteredItems = getAll(search);

		if (filteredItems == null || filteredItems.size() == 0) {
			this.filteredItems = getRoads(search);
		}

		if (filteredItems == null)
			return;

		Set<String> toString = new HashSet<>();
		for (Road r : this.filteredItems) {
			toString.add(r.getRoadName());
		}

		if (this.filter != null && this.filter.length() >= 1 && filteredItems.size() > 0) {
			j.setModel(new DefaultComboBoxModel(toString.toArray()));
			j.setSelectedItem(this.filter);
		}

		if (this.filteredItems != null) {
			for (Road road : this.filteredItems) {
				road.highlightRoad();
			}
		}
	}

	@Override
	protected void onMove(Move m) {

		this.mouseEvent = true;

		if (m == Move.ZOOM_IN) {

			double[] dimensions = getLocDimensions();
			double dx = (dimensions[0] - dimensions[0] * ZOOM_IN) / 2;
			double dy = (dimensions[1] - dimensions[1] * ZOOM_IN) / 2;
			origin = origin.moveBy(dx, dy);
			scale = (scale / ZOOM_IN);
			redraw();

		} else if (m == Move.ZOOM_OUT) {

			double[] dimensions = getLocDimensions();
			double dx = (dimensions[0] - dimensions[0] * ZOOM_OUT) / 2;
			double dy = (dimensions[1] - dimensions[1] * ZOOM_OUT) / 2;
			origin = origin.moveBy(dx, dy);
			scale = (scale / ZOOM_OUT);
			redraw();

		} else if (m == Move.WEST) {
			this.origin = new Location(this.origin.x - 1, this.origin.y);
		} else if (m == Move.EAST) {
			this.origin = new Location(this.origin.x + 1, this.origin.y);
		} else if (m == Move.NORTH) {
			this.origin = new Location(this.origin.x, this.origin.y + 1);
		} else if (m == Move.SOUTH) {
			this.origin = new Location(this.origin.x, this.origin.y - 1);
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons, File restrictions) {
		// load the file and convert each latitude and longitude to location and draw it
		// on the gui

		String inputLine;
		StringTokenizer parser;

		try (BufferedReader bufReader = new BufferedReader(new FileReader(roads))) {

			bufReader.readLine();
			while ((inputLine = bufReader.readLine()) != null && inputLine.length() != 0) {
				parser = new StringTokenizer(inputLine);
				int roadID = Integer.parseInt(parser.nextToken());
				int type = Integer.parseInt(parser.nextToken());
				String label = parser.nextToken("[^\t]+"); // read until a tab
				String city = parser.nextToken("[^\t]+");
				int oneway = Integer.parseInt(parser.nextToken());
				int speed = Integer.parseInt(parser.nextToken());
				int roadclass = Integer.parseInt(parser.nextToken());
				int notforcar = Integer.parseInt(parser.nextToken());
				int notforpede = Integer.parseInt(parser.nextToken());
				int notforbicycle = Integer.parseInt(parser.nextToken());
				allRoades.put(roadID, new Road(roadID, type, label, city, oneway, speed, roadclass, notforcar,
						notforpede, notforbicycle));
				this.trieRoot.add(label, allRoades.get(roadID));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (BufferedReader bufReader = new BufferedReader(new FileReader(nodes))) {

			while ((inputLine = bufReader.readLine()) != null && inputLine.length() != 0) {
				parser = new StringTokenizer(inputLine);
				int id = Integer.parseInt(parser.nextToken());
				double latitude = Double.parseDouble(parser.nextToken());
				double longitude = Double.parseDouble(parser.nextToken());
				Vertex node = new Vertex(id, Location.newFromLatLon(latitude, longitude), this, new Color(15, 65, 155));
				this.nodes.put(id, node);
				Location loc = Location.newFromLatLon(latitude, longitude);
				this.quadTree.insert(loc.asPoint(this.origin, this.scale), this.nodes.get(id));
			}
			this.makeNew = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (BufferedReader bufReader = new BufferedReader(new FileReader(segments))) {
			bufReader.readLine();
			while ((inputLine = bufReader.readLine()) != null && inputLine.length() != 0) {
				parser = new StringTokenizer(inputLine);
				int roadID = Integer.parseInt(parser.nextToken());
				Road road = allRoades.get(roadID);
				float length = Float.parseFloat(parser.nextToken());
				Vertex nodeID1 = this.nodes.get(Integer.parseInt(parser.nextToken()));
				Vertex nodeID2 = this.nodes.get(Integer.parseInt(parser.nextToken()));
				List<Location> segs = new ArrayList<>();
				while (parser.hasMoreTokens()) {
					double latitude = Double.parseDouble(parser.nextToken());
					double longitude = Double.parseDouble(parser.nextToken());
					segs.add(Location.newFromLatLon(latitude, longitude));
				}
				Segment s = new Segment(road, roadID, nodeID1, nodeID2, length, segs);
				allSegments.add(s);
				nodeID1.addOutgoing(s);
				nodeID1.addinGoing(s);
				nodeID2.addinGoing(s);
				nodeID2.addOutgoing(s);
				nodeID1.addSeg(s);
				nodeID2.addSeg(s);
				road.addSegment(s);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (polygons == null) // so can open the small data file
			return;

		int endLevel = -1;
		int cityIdx = -1;
		String hexCode = "";

		try (BufferedReader bufReader = new BufferedReader(new FileReader(polygons))) {

			while ((inputLine = bufReader.readLine()) != null) {
				if (inputLine.length() < 1)
					continue;

				parser = new StringTokenizer(inputLine);
				String str = parser.nextToken();

				if (str.startsWith("Type")) {
					hexCode = str.replaceAll("Type=", "");
				} else if (str.startsWith("EndLevel=")) {
					endLevel = Integer.parseInt(str.replaceAll("EndLevel=", ""));
				} else if (str.startsWith("CityIdx"))

					cityIdx = Integer.parseInt(str.replaceAll("CityIdx=", ""));
				else if (str.startsWith("Data0") || str.startsWith("Data1")) {
					str = str.replaceAll("Data0=", "");
					str = str.replaceAll("Data1=", "");
					str = str.replaceAll("[()]", ""); // remove brackets
					str = str.replaceAll(",(?!)", " "); // remove commas but only outside brackets
					str = str.replaceAll(",", " "); // replace all comma's with space

					String[] data = str.split("\\s+"); // split by space

					List<Location> list = new ArrayList<>();
					for (int i = 0; i < data.length - 1; i += 2) {
						Location loc = Location.newFromLatLon(Double.parseDouble(data[i]),
								Double.parseDouble(data[i + 1]));
						list.add(loc);
					}

					Polygon poly = new Polygon(hexCode, list, cityIdx, endLevel);

					if (endLevel == 3)
						this.endLevel3.add(poly);
					else if (endLevel == 2)
						this.endLevel2.add(poly);
					else if (endLevel == 1)
						this.endLevel1.add(poly);

					endLevel = -1;
					cityIdx = -1;
					hexCode = "";

				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (BufferedReader bufReader = new BufferedReader(new FileReader(restrictions))) {

			bufReader.readLine();
			while ((inputLine = bufReader.readLine()) != null) {
				parser = new StringTokenizer(inputLine);
				Vertex nodeID1 = this.nodes.get(Integer.parseInt(parser.nextToken()));
				Road roadID = this.allRoades.get(Integer.parseInt(parser.nextToken()));
				Vertex intersection = this.nodes.get(Integer.parseInt(parser.nextToken()));
				Road roadID1 = this.allRoades.get(Integer.parseInt(parser.nextToken()));
				Vertex nodeID2 = this.nodes.get(Integer.parseInt(parser.nextToken()));
				
				Segment between = nodeID1.getBetween(intersection);
				Segment from = nodeID1.getBetween(intersection);
				Segment to = intersection.getBetween(nodeID2);
				from.setRestricted(true);
				to.setRestricted(true);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private double[] getLocDimensions() {

		Point[] verticies = { new Point(0, 0), new Point(0, (int) getDrawingAreaDimension().getHeight()),
				new Point((int) getDrawingAreaDimension().getWidth(), 0),
				new Point((int) getDrawingAreaDimension().getWidth(), (int) getDrawingAreaDimension().getWidth()) };
		Location[] locations = new Location[4];
		for (int i = 0; i < 4; i++) {
			locations[i] = Location.newFromPoint(verticies[i], origin, scale);
		}
		double[] difference = new double[2];
		difference[0] = locations[2].x - locations[0].x; // width
		difference[1] = locations[1].y - locations[0].y; // height
		return difference;
	}

	@Override
	protected void onDrag(MouseEvent e) {
		this.mouseEvent = true;

		// get the location where the mouse was pressed and released, then call the
		// moveBy helper function in the location class to move to the evaluated
		// difference
		Location from = Location.newFromPoint(new Point(this.lastMouseX, this.lastMouseY), this.origin, this.scale);
		Location to = Location.newFromPoint(new Point(e.getX(), e.getY()), this.origin, this.scale);
		origin = origin.moveBy((from.x - to.x), (from.y - to.y));
		this.lastMouseX = e.getX();
		this.lastMouseY = e.getY();

	}

	/**
	 * 
	 * @param p
	 *            the point where we clicked
	 * @param root
	 * @param radius
	 * @return
	 */

	private Vertex getClosestPoint(Point p, QuadTree root, int radius) {

		Stack<QuadTree> stack = new Stack<>();
		stack.add(root);
		Point closest = null;
		Point bestsoFar = null;
		Vertex closestVertex = null;

		QuadTree q = null;
		while (!stack.isEmpty()) {

			double closestDistance = Double.MAX_VALUE;
			QuadTree node = stack.pop();

			if (node.isDivided()) {
				stack.addAll(node.getInRange(p, radius));
			} else { // it is a leaf, need to examine the points contained in it
				for (Point point : node.getPoints()) {
					if (bestsoFar == null) {
						bestsoFar = point;
						closestVertex = node.getNode(point);
						closestDistance = p.distance(point);
						q = node;
					} else if (p.distance(point) < closestDistance) {
						closestDistance = p.distance(point);
						bestsoFar = point;
						closestVertex = node.getNode(point);
					}
				}
				if (bestsoFar == null)
					continue;
				if (closest == null) {
					closest = bestsoFar;

				} else if (p.distance(bestsoFar) < p.distance(closest)) {
					q = node;
					closest = bestsoFar;

				}

			}
			radius += 100;
		}
		return q.getNode(closest);
	}

	public List<Road> getRoads(String word) {

		TrieNode root = this.trieRoot;
		TrieNode last = root;

		for (int i = 0; i < word.length(); i++) {
			last = root;
			char c = word.charAt(i);
			if (!root.getChildren().containsKey(c))
				return null;

			root = root.getChildren().get(c);
		}

		return last.getRoads();
	}

	public List<Road> getAll(String prefix) {

		List<Road> results = new ArrayList<>();

		TrieNode root = this.trieRoot;

		for (int i = 0; i < prefix.length(); i++) {
			char c = prefix.charAt(i);
			if (!root.getChildren().containsKey(c)) {
				return null;
			}
			root = root.getChildren().get(c);
		}

		getAllFrom(root, results);
		return results;

	}

	private void getAllFrom(TrieNode node, List<Road> results) {

		results.addAll(node.getRoads());

		for (TrieNode n : node.getChildren().values()) {
			getAllFrom(n, results);
		}
	}

	public static void main(String[] args) {

		RoadMap m = new RoadMap();
		m.redraw();
	}

	@Override
	protected void findShortestPath(String by) {

		// okk where we coming from is marked as restricted lane,, so have to check of
		// the lanes we planning to search is restricted or not, if
		// it is then cant go there

		if (this.selected == null || this.goal == null)
			return;

		if (this.highlightedPath.size() > 0) {
			for (Segment s : this.highlightedPath)
				s.setHighlight(false);
		}

		for (Vertex n : nodes.values()) {
			n.setVisited(false);
			n.setPrevious(null);
		}

		final Queue<Tuple> queue = new PriorityQueue<>(new VertexComparator());
		queue.add(new Tuple(this.selected, null, 0.0, this.selected.estimate(this.goal, by))); // add the root node

		while (!queue.isEmpty()) {
			Tuple current = queue.poll();
			Vertex pop = current.node;
			Vertex from = current.from;
			Double cost = current.cost;
			Double total = current.total;

			if (!pop.getVisited()) {
				pop.setVisited(true);
				pop.setPrevious(from);

				if (pop.equals(this.goal)) {
					highLightPath(pop, by);
					break; // found the goal node
				}

				for (Vertex v : pop.getNeighbours()) {
					Segment s = pop.getBetween(v);
					// get the parent of the popped element, so we know where are we coming from
					// then add additional check here
					if (pop.getPrevious() != null) {
						Segment s2 = pop.getBetween(pop.getPrevious());

						if (s2.isRestricted() && s.isRestricted())
							continue;
					}
					if (!v.getVisited()) {
						if (s.isOneWay()) { // if it is a one way road, then need to check
							if (!s.getFrom().equals(pop))
								continue;
						}
						double costToNeigh = cost + s.getCost(by);
						double totalEst = costToNeigh + v.estimate(this.goal, by);
						queue.add(new Tuple(v, pop, costToNeigh, totalEst));
						if (pop.getH() - v.getH() > v.getG()) {
							//System.out.println("Heuristic is not admissible");
						}
					}

				}
			}
		}

	}
	

	public void highLightPath(Vertex node, String by) {

		getTextOutputArea().append("\n");
		Vertex current = node;
		Vertex temp = current;
		
		Set<String> roadNames = new HashSet<>();
		
		String currentRoad = node.getBetween(node.getPrevious()).getName();
		String next;
		double total_Cost = 0;
		double current_Cost = 0;
		
		while (current.getPrevious() != null) {
			current = temp.getPrevious();
			Segment s = current.getBetween(temp);
			total_Cost += s.getCost("null");
			next = s.getName();
			this.highlightedPath.add(s);
			if(currentRoad.equals(next)) {
				current_Cost += s.getCost("null");
			}else {
				getTextOutputArea().append(s.getName()+"("+(current_Cost)+")" + "\n");
			    current_Cost =  s.getCost("null");
			    currentRoad = next;
			}
			if (s != null)
				s.setHighlight(true);
			temp = current;

		}

		getTextOutputArea().append("Total Distance = " +(total_Cost)+" KM");
	}
	
	public Collection<Vertex> getVertices(){
		return nodes.values();
	}

	
	@Override
	protected void findAllArticulationPoints() {
		
		if(!load_articulationPoints) {
			articulationPoints = this.algo.findAllArticulationPoints();
			load_articulationPoints = true;
		}

		if (new Color(119, 179, 234).equals(art.getBackground())) {
			art.setBackground(null);
			for (Vertex v : articulationPoints) {
				v.setColor(null);
			}

		} else {
			for (Vertex v : articulationPoints) {
				v.setColor(Color.MAGENTA);
				art.setBackground(new Color(119, 179, 234));
			}

		}
	}

}
