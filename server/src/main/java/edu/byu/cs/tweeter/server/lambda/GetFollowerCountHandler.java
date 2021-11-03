package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowerCountHandler implements RequestHandler<FollowerCountRequest, FollowerCountResponse> {

    @Override
    public FollowerCountResponse handleRequest(FollowerCountRequest request, Context context) {
        FollowService service = new FollowService();
        FollowerCountResponse response = service.followerCount(request);
        return response;
    }
}
