/**
 * Created by Anuved on 11/5/2016.
 *
 * @Class PaymoUser
 * This object stores information about a unique Paymo user.
 *
 * For now, the only useful information is the user's unique identifier (UserID),
 * but this can easily be extended to include data such as bank account info,
 * past transactions, etc.
 */
public class PaymoUser {

	/* Unique UserID for a PaymoUser */
	private Integer mUserID;

	/* Constructor for PaymoUser object */
	public PaymoUser(Integer userID) {
		mUserID = userID;
	}

	/* Returns unique PaymoUser UserID */
    public Integer getUserID() {
		return mUserID;
	}

	/*
	* Override equals and hash functions to ensure that users with identical UserIDs are evaluated the same, even
	* if they are stored as different objects in memory.
	* */
	@Override
	public boolean equals(Object object) {
		if(mUserID == ((PaymoUser) object).getUserID())
			return true;
		else return false;
	}

	@Override
	public int hashCode() {
		return mUserID.hashCode();
	}
}