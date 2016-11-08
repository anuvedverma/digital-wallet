import java.awt.*;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Anuved on 11/7/2016.
 *
 * @Class Vertex
 * Stores Paymo User data in the context of graph. Contains properties and methods necessary for graph algorithms.
 */
public class Vertex {

    /* Stores data for specific Paymo user */
    private PaymoUser mPaymoUser;

    /* Adjacency list to represent vertex neighbors in graph. HashMap used to allow O(1) lookup time */
    private HashMap<Integer, Vertex> mAdjList;

    /* Color tracks whether vertex has been visited during graph traversal */
    private Color mColor;

    /* Distance from source tracks number of edges between current vertex and source vertex during graph traversal */
    private Integer mDistanceFromSource;

    /* Parent tracks the immediate vertex from which this vertex was reached during graph traversal */
    private Vertex mParent;

    /* Constructor initializes values to default, unvisited vertex for a particular user */
    public Vertex(PaymoUser paymoUser) {

        // user&network related properties
        mPaymoUser = paymoUser;
        mAdjList = new HashMap<>();

        // graph-traversal related properties
        mColor = Color.WHITE;
        mDistanceFromSource = Integer.MAX_VALUE;
        mParent = null;
    }

    /* Getters */
    public PaymoUser getPaymoUser() {
        return mPaymoUser;
    }

    public Color getColor() {
        return mColor;
    }

    public Integer getDistanceFromSource() {
        return mDistanceFromSource;
    }

    public Vertex getParent() {
        return mParent;
    }

    public Collection<Vertex> getNeighbors() {
        return mAdjList.values();
    }


    /* Setters */
    public void setColor(Color color) {
        mColor = color;
    }

    public void setDistanceFromSource(Integer distanceFromSource) {
        mDistanceFromSource = distanceFromSource;
    }

    public void setParent(Vertex parent) {
        mParent = parent;
    }


    /* Adds neighbor to vertex's adjacency list */
    public void addNeighbor(Vertex neighbor) {
        mAdjList.put(neighbor.getPaymoUser().getUserID(), neighbor);
    }

    /* Checks if vertex contains a particular neighbor, either by vertex or by user */
    public boolean hasNeighbor(Vertex user) {
        return mAdjList.containsKey(user.getPaymoUser().getUserID());
    }

    public boolean hasNeighbor(PaymoUser user) {
        return mAdjList.containsKey(user.getUserID());
    }

    /* Resets graph-traversal properties of vertex */
    public void cleanVertex() {
        setColor(Color.WHITE);
        setDistanceFromSource(Integer.MAX_VALUE);
        setParent(null);
    }
    /*
    * Override equals and hash functions to ensure that vertices with the same users are evaluated the same.
    * */
    @Override
    public boolean equals(Object object) {
        if(mPaymoUser.getUserID() == ((Vertex) object).getPaymoUser().getUserID())
            return true;
        else return false;
    }

    @Override
    public int hashCode() {
        return mPaymoUser.getUserID().hashCode();
    }
}
