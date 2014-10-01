package mdpAlgorithm;

public class BFSGraph {
	public int count; // num of vertices
    private BFSNode vertices[];

    public BFSGraph()
    {
        vertices = new BFSNode[257];
        count = 0;
    }

    public void addNode(BFSNode n)
    {
        if(count < 257)
        {
            vertices[count] = n;
            count++;
        }
        else
        {
            System.out.println("graph full");
        }
    }

    public BFSNode[] getNode()
    {
        return vertices;
    }
}
