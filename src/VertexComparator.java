import java.util.Comparator;

public class VertexComparator implements Comparator<Tuple> {

	@Override
	public int compare(Tuple x, Tuple y) {
		
		if(x.total > y.total)return 1;
		if(y.total > x.total)return -1;
		return 0;
	}
	
	

}
