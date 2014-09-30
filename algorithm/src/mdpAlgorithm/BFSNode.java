package mdpAlgorithm;
import mdpAlgorithm.BFSState;

public class BFSNode {
	public BFSNode[] child;
    public int childCount;
    private String vertex;
    public BFSState state;

    public BFSNode(String vertex)
    {
        this.vertex = vertex;
    }

    public BFSNode(String vertex, int childlen)
    {
        this.vertex = vertex;
        childCount = 0;
        child = new BFSNode[childlen];
    }

    public void addChildNode(BFSNode adj)
    {
        adj.state = BFSState.Unvisited;
        if(childCount < 30)
        {
            this.child[childCount] = adj;
            childCount++;
        }
    }

    public BFSNode[] getChild()
    {
        return child;
    }

    public String getVertex()
    {
        return vertex;
    }
}
