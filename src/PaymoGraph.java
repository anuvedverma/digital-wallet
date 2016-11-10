import java.awt.*;
import java.util.*;

/**
 * Created by Anuved on 11/5/2016.
 *
 * @Class PaymoGraph
 * Used to track payment relationships between users and their respective friend networks.
 * PaymoUsers are represented as Vertices to allow for graph algorithms.
 */
public class PaymoGraph {

	/* Stores all vertices in HashMap for quick lookup.
	*  Key = unique User ID Value = Vertex object containing PaymoUser and graph vertex properties
	*/
	private HashMap<Integer, Vertex> mVertices = new HashMap<>();

	/* Received new PaymoTransaction, extracts users from it, and adds/connects vertices to graph accordingly */
	public void addTransaction(PaymoTransaction newTransaction) {

		// get users from transaction
		PaymoUser user1 = newTransaction.getPaymoUser1();
		PaymoUser user2 = newTransaction.getPaymoUser2();

		// connect vertices
		connectVertices(user1,user2);
	}

	/* Adds user vertex to graph if it doesn't already exist */
	public void addVertex(PaymoUser user) {
		if(mVertices.containsKey(user.getUserID())) // check is unnecessary with HashMap, but kept for readability
			return;
		mVertices.put(user.getUserID(), new Vertex(user));
	}

	/* Connects two users by adding them to each other's adjacency lists (assuming both are already in graph) */
	public void connectVertices(PaymoUser user1, PaymoUser user2) {

		// add vertices to graph if not already in it
		addVertex(user1);
		addVertex(user2);

		// get vertices of interest from the graph
		Vertex user1Vertex = mVertices.get(user1.getUserID());
		Vertex user2Vertex = mVertices.get(user2.getUserID());

		// add both vertices to each other's adjacency lists to connect them (ie. add edge)
		user1Vertex.addNeighbor(user2Vertex);
		user2Vertex.addNeighbor(user1Vertex);
	}


	/* Checks whether two users are within a certain number of degree from each other */
	public boolean withinDegree(PaymoUser user1, PaymoUser user2, int degree) {

		// get user vertices from HashMap
		Vertex user1Vertex = mVertices.get(user1.getUserID());
		Vertex user2Vertex = mVertices.get(user2.getUserID());

		// checks degree number so that we implement optimal algorithm
		if(degree > 2)
			return isWithinXDegrees(user1Vertex, user2Vertex, degree); // feature 3
		else if(degree == 2)
			return isWithinTwoDegrees(user1Vertex, user2Vertex); // feature 2
		else if(degree == 1)
			return isWithinOneDegree(user1Vertex, user2Vertex); // feature 1
		else if(degree == 0)
			return user1.equals(user2);
		else
			return false;
	}

	/* Checks if two users are each other's neighbors in O(1) lookup */
	private boolean isWithinOneDegree(Vertex user1Vertex, Vertex user2Vertex) {
		return user1Vertex.hasNeighbor(user2Vertex);
	}

	/* Checks if two users are 2nd degree neighbors in O(n) search and lookup */
	private boolean isWithinTwoDegrees(Vertex user1Vertex, Vertex user2Vertex) {

		// if they're within 1 degree, then they're within 2 degree
		if(isWithinOneDegree(user1Vertex, user2Vertex))
			return true;

		// else iterate through user1's neighbors to see if they are neighbors with user2
		for(Vertex friendVertex : user1Vertex.getNeighbors()) {
			if(user2Vertex.hasNeighbor(friendVertex))
				return true;
		}
		return false;
	}

	/* Using BFS algorithm, searches up to @param degrees away from user1 to check if user2 is within the network */
	private boolean isWithinXDegrees(Vertex user1Vertex, Vertex user2Vertex, int degree) {

		// if within 1 or 2 degrees, then also within all degree > 2
		if(isWithinOneDegree(user1Vertex, user2Vertex))
			return true;

		if(isWithinTwoDegrees(user1Vertex, user2Vertex))
			return true;

		// init verification false- is set to true if user2 is found within user1's network
        boolean verified = false;

		/* Start BFS Algorithm */
		Queue<Vertex> queue = new LinkedList<>(); // tracks all neighbors to visit layer-by-layer
		Queue<Vertex> nodesToClean = new LinkedList<>(); // keep track of all nodes visited for cleanup later

		// init source vertex
		Vertex source = user1Vertex;
		Vertex current = source;
		current.setColor(Color.GRAY);
		current.setDistanceFromSource(0);
		current.setParent(null);

		// add vertex to queue and also track all visited nodes for resetting after BFS
		queue.add(current);
		nodesToClean.add(current);

		// run BFS until network is exhausted or we've extended beyond network of interest
		while (!queue.isEmpty() && current.getDistanceFromSource() + 1 < degree) {

			// get next vertex in queue
			current = queue.poll();

			// check if user2 is neighbor of current vertex
			if(current.hasNeighbor(user2Vertex))
				verified = true;

			// if user2 isn't found, add the unvisited children of current node to the queue
			Vertex parent = current;
			for (Vertex neighbor : current.getNeighbors()) {
				if (neighbor.getColor() == Color.WHITE) {
					queue.add(neighbor);
					nodesToClean.add(neighbor);
					neighbor.setColor(Color.GRAY);
					neighbor.setDistanceFromSource(parent.getDistanceFromSource() + 1);
					neighbor.setParent(parent);
				}
			}
		}

		// reset vertices touched by BFS
        for(Vertex v : nodesToClean)
			v.cleanVertex();

		return verified;
	}

	/* Check if graph contains a particular user, based on unique User ID */
	public boolean contains(PaymoUser user) {
		return mVertices.get(user.getUserID()) != null;
	}

	/* Return the vertex corresponding with a particular user */
	public Vertex getVertex(PaymoUser user) {
		return mVertices.get(user.getUserID());
	}

	/* Tracks size of graph in terms of number of vertices */
	public int numVertices() {
		return mVertices.size();
	}

	/* Returns an iterable of all vertices in graph */
	public Collection<Vertex> getVertices() {
		return mVertices.values();
	}
}