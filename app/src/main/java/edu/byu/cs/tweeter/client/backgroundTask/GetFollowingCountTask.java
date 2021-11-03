package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    private static final String URL_PATH = "/getfollowingcount";

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    private ServerFacade serverFacade;

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    @Override
    protected int runCountTask() throws IOException, TweeterRemoteException {
        getTargetUser().setImageBytes(null);
        FollowingCountRequest followingCountRequest = new FollowingCountRequest(getAuthToken(), getTargetUser(),20);
        FollowingCountResponse response = getServerFacade().followingCount(followingCountRequest, URL_PATH);

        return response.getCount();
    }
}

