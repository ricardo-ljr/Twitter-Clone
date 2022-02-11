import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class GetFollowersTest {

    private FollowerRequest request;
    private FollowerRequest invalidRequest;
    private FollowerResponse successResponse;
    private FollowerResponse invalidResponse;
    private ServerFacade serverFacade;

    private static final String URL_PATH = "/getfollowers";
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";

    @Before
    public void setup() throws IOException, TweeterRemoteException {
        AuthToken authToken = new AuthToken();
        User user = new User("Allen", "Anderson", "@allen", MALE_IMAGE_URL);
        User user2 = new User("Bob", "Bobson", "@bob", MALE_IMAGE_URL);
        User user3 = new User("Chris", "Colston", "@chris", MALE_IMAGE_URL);

        request = new FollowerRequest(authToken, user.getAlias(),3, null);
        invalidRequest = new FollowerRequest(null, null, 0, null);

        successResponse = new FollowerResponse(Arrays.asList(user, user2, user3), false);
        invalidResponse = new FollowerResponse("Something went wrong");

        serverFacade = Mockito.spy(new ServerFacade());

        Mockito.when(serverFacade.getFollowers(request, URL_PATH)).thenReturn(successResponse);
        Mockito.when(serverFacade.getFollowers(invalidRequest, URL_PATH)).thenReturn(invalidResponse);
    }

    @Test
    public void testGetFollowersSuccessful() throws IOException, TweeterRemoteException {
        FollowerResponse response = serverFacade.getFollowers(request, URL_PATH);
        assertEquals(successResponse.isSuccess(), response.isSuccess());
    }

    @Test
    public void testRegisterFails() throws IOException, TweeterRemoteException {
        FollowerResponse response = serverFacade.getFollowers(invalidRequest, URL_PATH);
        assertEquals(invalidResponse.isSuccess(), response.isSuccess());
    }

}
