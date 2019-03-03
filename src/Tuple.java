
public class Tuple {
    public Vertex node;
    public Vertex from;
    public Double cost;
    public Double total;

    public Tuple(Vertex node, Vertex from, Double cost, Double total) {
        this.node = node;
        this.from = from;
        this.cost = cost;
        this.total = total;
    }
}