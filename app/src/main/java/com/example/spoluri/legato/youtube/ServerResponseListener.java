package com.example.spoluri.legato.youtube;
import com.example.spoluri.legato.AppConstants;

/**
 * Interface that contains method to be implemented by listeners to provide activity specific details to the background tasks
 */
public interface ServerResponseListener extends AppConstants {
	void prepareRequest(Object... objects);
	void goBackground(Object... objects);
	void completedRequest(Object... objects);

}
