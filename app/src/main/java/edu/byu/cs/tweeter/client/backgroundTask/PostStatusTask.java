package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthorizedTask {

    private static final String URL_PATH = "/post";

    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken, messageHandler);
        this.status = status;
    }

    private ServerFacade serverFacade;

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    @Override
    public void runTask() throws IOException, TweeterRemoteException {
        // TODO: Getting an Error when Posting
        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.

        PostStatusRequest postStatusRequest = new PostStatusRequest(status);
        PostStatusResponse response = getServerFacade().postStatus(postStatusRequest, URL_PATH);
        //

        if (response.isSuccess()) {
            return;
        } else {
            sendFailedMessage(response.getMessage());
        }

        // Let the user know if it successfully posted
    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        // Nothing to load
    }
}