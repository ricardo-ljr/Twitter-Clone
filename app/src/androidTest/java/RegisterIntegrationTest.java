import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

import static org.junit.Assert.assertEquals;

public class RegisterIntegrationTest {

    private RegisterRequest request;
    private RegisterRequest invalidRequest;

    private RegisterResponse successResponse;
    private RegisterResponse failedResponse;
    private ServerFacade serverFacade;

    private UserService userService;

    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";

    @Before
    public void setup() throws IOException, TweeterRemoteException {
        User user = new User("Allen", "Anderson", "@allen", MALE_IMAGE_URL);
        user.setPassword("password");
        User user2 = new User("Bob", "Bobson", "@bob", MALE_IMAGE_URL);
        user2.setPassword("password");


        request = new RegisterRequest(user.getAlias(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getImageUrl());
        invalidRequest = new RegisterRequest(user2.getAlias(), user.getPassword(), user2.getFirstName(), user2.getLastName(), user2.getImageUrl());

        successResponse = new RegisterResponse(user, new AuthToken());
        failedResponse = new RegisterResponse(false, "User could not be created");

        serverFacade = Mockito.spy(new ServerFacade());
        Mockito.when(serverFacade.register(request, "/register")).thenReturn(successResponse);
        Mockito.when(serverFacade.register(invalidRequest, "/register")).thenReturn(failedResponse);

    }

    @Test
    public void testRegisterSuccessful() throws IOException, TweeterRemoteException {
        RegisterResponse response = serverFacade.register(request, "/register");
        assertEquals(successResponse.isSuccess(), response.isSuccess());
    }

    @Test
    public void testRegisterFails() throws IOException, TweeterRemoteException {
        RegisterResponse response = serverFacade.register(invalidRequest, "/register");
        assertEquals(failedResponse.isSuccess(), response.isSuccess());
    }
}
