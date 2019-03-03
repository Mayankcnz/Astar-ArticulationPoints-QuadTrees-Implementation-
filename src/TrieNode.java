import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrieNode {
	
	private List<Road> roads = new ArrayList<>();
	private Map<Character,TrieNode> children = new HashMap<>();;
	
	public boolean add(String word, Road road) {
		
		if(word.length() < 1)return false;
		
			char c = word.charAt(0);
			if(!children.containsKey(c)) {
				TrieNode t = new TrieNode();
				children.put(c, t);
			}
			if(children.get(c).add(word.substring(1), road))return true;
		
		return roads.add(road);
	}
	
	
	public List<Road> getAll(String prefix){
		
		List<Road> results = new ArrayList<>();
		
		for(int i = 0; i < prefix.length(); i++) {
			char c = prefix.charAt(i);
			if(!children.containsKey(c)) {
				return null;
			}
			List<Road> list = children.get(c).getAll(prefix.substring(i+1));
			if(list != null) {
				return list;
			}else {
				break;
			}
		}
		getAllFrom(results);
		return results;
	}
	
	public Map<Character, TrieNode> getChildren(){
		return this.children;
	}
	
	public List<Road> getRoads(){
		return this.roads;
	}
	
	
	public void getAllFrom(List<Road> results) {
		
		results.addAll(roads);
		
		
		for (TrieNode n : children.values())
			 n.getAllFrom(results);
	}
	
}
