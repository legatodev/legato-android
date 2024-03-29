package com.legato.music.youtube;
import com.legato.music.AppConstants;

/**
 * Interface that contains method to be implemented by listeners to provide activity specific details to the background tasks
 */
interface ServerResponseListener extends AppConstants {
	void prepareRequest(Object... objects);
	void goBackground(Object... objects);
	void completedRequest(Object... objects);

}
