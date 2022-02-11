import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

public class GetFollowingCountTest {

    private FollowingCountRequest request;
    private FollowingCountRequest invalidRequest;

    private FollowingCountResponse successResponse;
    private FollowingCountResponse failedResponse;
    private ServerFacade serverFacade;

    private static final String URL_PATH = "/getfollowingcount";
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";


    @Before
    public void setup() throws IOException, TweeterRemoteException {
        AuthToken authToken = new AuthToken();
        User user = new User("Allen", "Anderson", "@allen", MALE_IMAGE_URL);
        User user2 = new User("Bob", "Bobson", "@bob", MALE_IMAGE_URL);
        User user3 = new User("Chris", "Colston", "@chris", MALE_IMAGE_URL);

        request = new FollowingCountRequest(authToken, user, 3);
        invalidRequest = new FollowingCountRequest(null, null, 0);

        successResponse = new FollowingCountResponse(user, authToken, 3);
        failedResponse = new FollowingCountResponse(null, null, 0);

        serverFacade =
                Mockito.spy(new ServerFacade());

        Mockito.when(serverFacade.followingCount(request, URL_PATH)).thenReturn(successResponse);
        Mockito.when(serverFacade.followingCount(invalidRequest, URL_PATH)).thenReturn(failedResponse);

    }

    @Test
    public void testGetFollowingCountSuccessful() throws IOException, TweeterRemoteException {
        FollowingCountResponse response = serverFacade.followingCount(request, URL_PATH);
        assertEquals(successResponse.isSuccess(), response.isSuccess());
    }

    @Test
    public void testRegisterFails() throws IOException, TweeterRemoteException {
        FollowingCountResponse response = serverFacade.followingCount(invalidRequest, URL_PATH);
        assertEquals(failedResponse.isSuccess(), response.isSuccess());
    }

}
