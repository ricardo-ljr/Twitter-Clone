package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.UserService;

public class LogoutHandler implements RequestHandler<LogoutRequest, LogoutResponse> {
    @Override
    public LogoutResponse handleRequest(LogoutRequest request, Context context) {
        DynamoDBDAOFactoryInterface factory = HandlerConfig.getInstance().getFactory();
        UserService service = new UserService(factory);
        try {
            return service.logout(request);
        } catch (RuntimeException e) {
            String message = "[BadRequest]";
            throw new RuntimeException(message, e);
        }
    }
}
