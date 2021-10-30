package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    private static final String URL_PATH = "/getfollowers";

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    private ServerFacade serverFacade;

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() throws IOException, TweeterRemoteException {
        String targetUserAlias = (getTargetUser() == null) ? null : getTargetUser().getAlias();
        String lastFolloweeAlias = (getLastItem() == null) ? null : getLastItem().getAlias();
        FollowerRequest followerRequest = new FollowerRequest(getAuthToken(), targetUserAlias, getLimit(), lastFolloweeAlias);
        FollowerResponse response = getServerFacade().getFollowers(followerRequest, URL_PATH);
        return new Pair<>(response.getFollowers(), response.getHasMorePages());
    }
}

