package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowHandler implements RequestHandler<FollowRequest, FollowResponse> {

    @Override
    public FollowResponse handleRequest(FollowRequest requet, Context context) {
        FollowService service = new FollowService();
        FollowResponse response = service.follow(requet);
        return response;
    }
}
