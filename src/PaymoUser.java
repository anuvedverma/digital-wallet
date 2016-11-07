/**
 * Created by Anuved on 11/5/2016.
 */
public class PaymoUser {

	// declare member variables
	private Integer mUserID;

	public PaymoUser(Integer userID) {
		mUserID = userID;
	}

    public Integer getUserID() {
		return mUserID;
	}

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