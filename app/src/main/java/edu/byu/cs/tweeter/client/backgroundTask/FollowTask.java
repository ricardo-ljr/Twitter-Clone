package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthorizedTask {

    private static final String URL_PATH = "/follow";

    /**
     * The user that is being followed.
     */
    private final User followee;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    private ServerFacade serverFacade;

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    @Override
    protected void runTask() throws IOException, TweeterRemoteException {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.

        try {
            FollowRequest followRequest = new FollowRequest(getAuthToken(), followee, Cache.getInstance().getCurrUser());
            FollowResponse response = getServerFacade().follow(followRequest, URL_PATH);

            if (response.isSuccess()) {
                BackgroundTaskUtils.loadImage(followRequest.getTargetUser());
            } else {
                sendFailedMessage("Failed to follow user");
            }
        } catch (Exception e) {
            sendExceptionMessage(e);
        }

    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        // Nothing to load
    }
}
