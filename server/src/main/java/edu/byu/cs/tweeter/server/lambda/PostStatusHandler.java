package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {

    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        DynamoDBDAOFactoryInterface factory = HandlerConfig.getInstance().getFactory();
        StatusService service = new StatusService(factory);
        try {
            return service.postStatus(request);
        } catch (RuntimeException e) {
            String message = "[BadRequest]";
            throw new RuntimeException(message, e);
        }
    }
}
