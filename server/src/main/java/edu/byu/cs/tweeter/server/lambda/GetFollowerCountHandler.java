package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.UserService;

public class GetFollowerCountHandler implements RequestHandler<FollowerCountRequest, FollowerCountResponse> {

    @Override
    public FollowerCountResponse handleRequest(FollowerCountRequest request, Context context) {
        DynamoDBDAOFactoryInterface factory = HandlerConfig.getInstance().getFactory();
        UserService service = new UserService(factory);
        FollowerCountResponse response = service.getFollowerCount(request);
        try {
            return response;
        } catch (RuntimeException e) {
            String message = "[BadRequest]";
            throw new RuntimeException(message, e);
        }
    }
}
