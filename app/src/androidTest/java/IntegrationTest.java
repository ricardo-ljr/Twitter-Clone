import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.os.Looper;

import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.PostStatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;


public class IntegrationTest {

    private static final String STORY_URL_PATH = "/getstory";

    private LoginRequest validLoginRequest;
    private LoginResponse loginResponse;

    private PostStatusRequest validStatusRequest;
    private PostStatusResponse statusResponse;

    private ServerFacade serverFacade;

    ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    private MainPresenter.View mockMainView;
    private PostStatusService postStatusServiceSpy;

    private LoginPresenter.View mockLoginView;
    private UserService userServiceSpy;

    private MainPresenter mainPresenterSpy;
    private LoginPresenter loginPresenterSpy;

    private LoginTask validRequest_loginTaskSpy;
    private GetLoginObserver loginObserver;
    private User user;

    private User user1;
    private User user2;
    private AuthToken authToken;

    private PostStatusRequest validPostStatuRequest;
    private PostStatusResponse validPostStautsResponse;

    private GetPostStatusObserver postStatusObserver;
    private PostStatusTask validRequest_PostStatusTaskSpy;
    private Status resultStatus1;

    private CountDownLatch countDownLatch;

    private static final String URL_PATH = "/login";
    private String imageUrl = "https://mycs340bucket.s3.us-west-1.amazonaws.com/%40alias.jpg";
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";

    private StoryRequest storyRequest;
    private StoryResponse storyResponse;

    @Before
    public void setup() throws IOException, TweeterRemoteException, ParseException {

        // LOGIN USER
        User user = new User("First", "Last", "@alias", imageUrl);
        user.setPassword("password");

        validLoginRequest = new LoginRequest(user.getAlias(), user.getPassword());
        loginResponse = new LoginResponse(user, new AuthToken());

        mockLoginView = Mockito.mock(LoginPresenter.View.class);

        loginObserver = new GetLoginObserver();
        resetCountDownLatch();

        UserService userService = new UserService(loginObserver);
        userServiceSpy = Mockito.spy(userService);

        LoginTask validRequest_LoginTask = new LoginTask(user.getAlias(), user.getPassword(), new UserService.LoginHandler(Looper.getMainLooper(), loginObserver));

        Pair<User, AuthToken> successResponse_login = new Pair<>(loginResponse.getUser(), loginResponse.getAuthToken());

        // Create a main presenter spy so we can mock it with the userServiceMock
        loginPresenterSpy = Mockito.spy(new LoginPresenter(mockLoginView));

        validRequest_loginTaskSpy = Mockito.spy(validRequest_LoginTask);

        Mockito.when(validRequest_loginTaskSpy.runAuthenticationTask()).thenReturn(successResponse_login); // serverFacade.login()
        Mockito.when(userServiceSpy.getGetLoginTask(user.getAlias(), user.getPassword(), loginObserver)).thenReturn(validRequest_loginTaskSpy);

        // Post Status Here
        user1 = new User("Allen", "Anderson", MALE_IMAGE_URL);
        user2 = new User("Bob", "Bobson", "@bob", MALE_IMAGE_URL);

        String url = "https://byu.edu";
        List<String> urls = Arrays.asList(url);
        List<String> mentions1 = Arrays.asList(user1.getAlias());
        List<String> mentions2 = Arrays.asList(user2.getAlias());
        authToken = new AuthToken();

        resultStatus1 = new Status("Hello", user,"time", urls, mentions2 );
        Status resultStatus2 = new Status("Hello2", user2,"time2", urls, mentions1 );

        validPostStatuRequest = new PostStatusRequest(resultStatus1);
        validPostStautsResponse = new PostStatusResponse(resultStatus1);

        mockMainView = Mockito.mock(MainPresenter.View.class);

        postStatusObserver = new GetPostStatusObserver();
        resetCountDownLatch();

        PostStatusService postStatusService = new PostStatusService(postStatusObserver);
        postStatusServiceSpy = Mockito.spy(postStatusService);

        PostStatusTask validRequest_PostStatusTask = new PostStatusTask(authToken, resultStatus1, new PostStatusService.PostStatusHandler(Looper.getMainLooper(), postStatusObserver));

        mainPresenterSpy = Mockito.spy( new MainPresenter(mockMainView));

        validRequest_PostStatusTaskSpy = Mockito.spy(validRequest_PostStatusTask);

//        Mockito.doReturn(postStatusService).when(mainPresenterSpy).getPostStatusService(postStatusObserver);

        storyRequest = new StoryRequest(new AuthToken(), user.getAlias(), 10, null);


    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    private class GetLoginObserver implements UserService.LoginObserver {
        private User user;
        private AuthToken authtoken;

        @Override
        public void handleSuccess(User user, AuthToken authToken) {
            this.user = user;
            this.authtoken = authToken;
            countDownLatch.countDown();
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public AuthToken getAuthtoken() {
            return authtoken;
        }

        public void setAuthtoken(AuthToken authtoken) {
            this.authtoken = authtoken;
        }

        @Override
        public void handleFailureObserver(String message) {

        }

        @Override
        public void handleExceptionObserver(Exception exception) {

        }
    }

    private class GetPostStatusObserver implements PostStatusService.PostStatusObserver {

        String message;

        @Override
        public void handleSuccessPostStatus(String message) {
            this.message = message;
        }

        @Override
        public void handleFailureObserver(String message) {

        }

        @Override
        public void handleExceptionObserver(Exception exception) {

        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Test
    public void postStatusTest() throws IOException, ParseException, TweeterRemoteException, InterruptedException {

        // Login initial user
        userServiceSpy.login("@alias", "password", loginObserver); // login works

        awaitCountDownLatch(); // await

        assertEquals(loginResponse.getUser(), loginObserver.getUser()); // asserting response is equal

        // Post Status from user @alias
        Answer<Void> callHandleSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                postStatusObserver.handleSuccessPostStatus("Successfully Posted!");
                return null;
            }
        };

        Mockito.doAnswer(callHandleSucceededAnswer).when(postStatusServiceSpy).postStatus(resultStatus1.getPost(), postStatusObserver);

        mainPresenterSpy.postStatus(resultStatus1.getPost());

        // Mocked view
        //

        // If it doesn't pass the first time it means it timed out
        TimeUnit.SECONDS.sleep(2);
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");

        TimeUnit.SECONDS.sleep(2);
        Mockito.verify(mockMainView).displayInfoMessage("Successfully Posted!");

        assertNotNull(validPostStautsResponse.getStatus());

        // Get story and confirm it's in database
        StoryResponse storyResponse = getServerFacade().getStory(storyRequest, STORY_URL_PATH);

        // Indexing and getting the last Status posted
        assertEquals(storyResponse.getStatuses().get(storyResponse.getStatuses().size() - 1).getPost(), resultStatus1.getPost());

    }


}
