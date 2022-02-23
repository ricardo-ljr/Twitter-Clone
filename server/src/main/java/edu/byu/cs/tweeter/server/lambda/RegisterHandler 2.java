package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterHandler implements RequestHandler<RegisterRequest, RegisterResponse> {

    @Override
    public RegisterResponse handleRequest(RegisterRequest registerRequest, Context context) {
        DynamoDBDAOFactoryInterface factory = HandlerConfig.getInstance().getFactory();
        UserService userService = new UserService(factory);
        try {
            return userService.register(registerRequest);
        } catch (RuntimeException | DataAccessException e) {
            String message = "[BadRequest]";
            throw new RuntimeException(message, e);
        }
    }
}
