package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowHandler implements RequestHandler<FollowRequest, FollowResponse> {

    @Override
    public FollowResponse handleRequest(FollowRequest request, Context context) {
        DynamoDBDAOFactoryInterface factory = HandlerConfig.getInstance().getFactory();
        FollowService service = new FollowService(factory);
        FollowResponse response = service.follow(request);
        try {
            return response;
        } catch (RuntimeException e) {
            String message = "[BadRequest]";
            throw new RuntimeException(message, e);
        }
    }
}
