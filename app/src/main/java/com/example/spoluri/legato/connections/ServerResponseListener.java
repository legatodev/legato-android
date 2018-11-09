package com.example.spoluri.legato.connections;
import com.example.spoluri.legato.AppConstants;

/**
 * Interface that contains method to be implemented by listeners to provide activity specific details to the background tasks
 */
public interface ServerResponseListener extends AppConstants {
	public void prepareRequest(Object... objects);
	public void goBackground(Object... objects);
	public void completedRequest(Object... objects);

}
