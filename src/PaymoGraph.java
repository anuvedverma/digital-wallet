import java.awt.*;
import java.util.*;

/**
 * Created by Anuved on 11/5/2016.
 */
public class PaymoGraph {

	private HashMap<Integer, Vertex> mVertices = new HashMap<>();

	public void addTransaction(PaymoTransaction newTransaction) {
		PaymoUser user1 = newTransaction.getPaymoUser1();
		PaymoUser user2 = newTransaction.getPaymoUser2();

		addVertex(user1);
		addVertex(user2);

		connectVertices(user1,user2);
	}

	public void addVertex(PaymoUser user) {
		if(mVertices.containsKey(user.getUserID()))
			return;
		mVertices.put(user.getUserID(), new Vertex(user));
	}

	private void connectVertices(PaymoUser user1, PaymoUser user2) {

		boolean bothExist = (mVertices.containsKey(user1.getUserID())) && (mVertices.containsKey(user2.getUserID()));
//		boolean firstExists = (mVertices.containsKey(user1.getUserID())) && (!mVertices.containsKey(user2.getUserID()));
//		boolean secondExists = (!mVertices.containsKey(user1.getUserID())) && (mVertices.containsKey(user2.getUserID()));
//		boolean noneExist = (!mVertices.containsKey(user1.getUserID())) && (!mVertices.containsKey(user2.getUserID()));

		Vertex user1Vertex;
		Vertex user2Vertex;

		if(bothExist) {
			user1Vertex = mVertices.get(user1.getUserID());
			user2Vertex = mVertices.get(user2.getUserID());

			user1Vertex.addNeighbor(user2Vertex);
			user2Vertex.addNeighbor(user1Vertex);
			mVertices.put(user1.getUserID(), user1Vertex);
			mVertices.put(user2.getUserID(), user2Vertex);

		}
//		else if(firstExists) {
//			user1Vertex = mVertices.get(user1.getUserID());
//			user2Vertex = new Vertex(user2);
//		}
//		else if(secondExists) {
//			user1Vertex = new Vertex(user1);
//			user2Vertex = mVertices.get(user2.getUserID());
//		}
//		else { // if noneExist
//			user1Vertex = new Vertex(user1);
//			user2Vertex = new Vertex(user2);
//		}

//		user1Vertex.addNeighbor(user2Vertex);
//		user2Vertex.addNeighbor(user1Vertex);
//		mVertices.put(user1.getUserID(), user1Vertex);
//		mVertices.put(user2.getUserID(), user2Vertex);
	}


	public boolean withinDegree(PaymoUser user1, PaymoUser user2, int degree) {

		Vertex user1Vertex = mVertices.get(user1.getUserID());
		Vertex user2Vertex = mVertices.get(user2.getUserID());

		if(degree < 2)
			return feature1(user1Vertex, user2Vertex);
		else if(degree == 2)
			return feature2(user1Vertex, user2Vertex);
		else
			return feature3(user1Vertex, user2Vertex, degree);
	}

	private boolean feature1(Vertex user1Vertex, Vertex user2Vertex) {
		return user1Vertex.hasNeighbor(user2Vertex);
	}

	private boolean feature2(Vertex user1Vertex, Vertex user2Vertex) {

		if(feature1(user1Vertex, user2Vertex))
			return true;

		for(Vertex friendVertex : user1Vertex.getNeighbors()) {
			if(user2Vertex.hasNeighbor(friendVertex))
				return true;
		}
		return false;
	}

	private boolean feature3(Vertex user1Vertex, Vertex user2Vertex, int degree) {

		if(feature1(user1Vertex, user2Vertex))
			return true;

		if(feature2(user1Vertex, user2Vertex))
			return true;

        boolean verified = false;

		// start BFS
		Queue<Vertex> queue = new LinkedList<>();
		Queue<Vertex> nodesToClean = new LinkedList<>();

		// init source
		Vertex source = user1Vertex;
		Vertex current = source;
		current.setColor(Color.GRAY);
		current.setDistanceFromSource(0);
		current.setParent(null);
		queue.add(current);
		nodesToClean.add(current);

		// run BFS
		while (!queue.isEmpty() && current.getDistanceFromSource() + 1 < degree) {

			current = queue.poll();

			if(current.hasNeighbor(user2Vertex))
				verified = true;

			Vertex parent = current;
			for(Vertex neighbor : current.getNeighbors()) {
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
        for(Vertex v : nodesToClean) {
			v.setColor(Color.WHITE);
			v.setDistanceFromSource(Integer.MAX_VALUE);
			v.setParent(null);
		}

		return verified;
	}


	public boolean contains(PaymoUser user) {
		return mVertices.get(user.getUserID()) != null;
	}

	public Vertex getVertex(PaymoUser user) {
		return mVertices.get(user.getUserID());
	}

	public int numVertices() {
		return mVertices.size();
	}

	public Collection<Vertex> getVertices() {
		return mVertices.values();
	}
}

class Vertex {
	private PaymoUser mPaymoUser;
	private HashMap<Integer, Vertex> mAdjList;
	private Color mColor;
	private Integer mDistanceFromSource;
	private Vertex mParent;

	public Vertex(PaymoUser paymoUser) {
		mPaymoUser = paymoUser;
		mAdjList = new HashMap<>();
		mColor = Color.WHITE;
		mDistanceFromSource = Integer.MAX_VALUE;
		mParent = null;
	}

	public PaymoUser getPaymoUser() {
		return mPaymoUser;
	}

	public Color getColor() {
		return mColor;
	}

	public void setColor(Color color) {
		mColor = color;
	}

	public Integer getDistanceFromSource() {
		return mDistanceFromSource;
	}

	public void setDistanceFromSource(Integer distanceFromSource) {
		mDistanceFromSource = distanceFromSource;
	}

	public Vertex getParent() {
		return mParent;
	}

	public void setParent(Vertex parent) {
		mParent = parent;
	}

	public Collection<Vertex> getNeighbors() {
		return mAdjList.values();
	}

	public void addNeighbor(Vertex neighbor) {
		mAdjList.put(neighbor.getPaymoUser().getUserID(), neighbor);
	}

	public boolean hasNeighbor(Vertex user) {
		return mAdjList.containsKey(user.getPaymoUser().getUserID());
	}

	public boolean hasNeighbor(PaymoUser user) {
		return mAdjList.containsKey(user.getUserID());
	}

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
