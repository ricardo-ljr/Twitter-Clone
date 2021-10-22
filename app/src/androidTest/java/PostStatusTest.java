import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.net.MalformedURLException;
import java.text.ParseException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.PostStatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Message;

public class PostStatusTest {

    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";

    private MainPresenter.View mockMainView;
    private PostStatusService mockPostStatusService;
    private Cache mockCache;

    private MainPresenter mainPresenterSpy;

    private final User user1 = new User("Allen", "Anderson", MALE_IMAGE_URL);
    private final AuthToken fakeAuthToken = new AuthToken("test-authtoken");

    @Before
    public void setup() {
        // Create mock MainPresenter dependencies
        mockMainView = Mockito.mock(MainPresenter.View.class);
        mockPostStatusService = Mockito.mock(PostStatusService.class);
        mockCache = Mockito.mock(Cache.class);

        // Create a main presenter spy so we can mock it with the userServiceMock
        mainPresenterSpy = Mockito.spy( new MainPresenter(mockMainView) );
        Mockito.doReturn(mockPostStatusService).when(mainPresenterSpy).getPostStatusService();

        // Inject a mock cache into the Cache class
        Cache.setInstance(mockCache);

    }

    @Test
    public void test_PostStatusSuccess() throws MalformedURLException, ParseException {
        // Create an answer object to make the mock UserService call handleSucceeded on it's
        // observer when it's logout method is called
        Answer<Void> callHandleSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals(invocation.getArgument(0), "Hello there");
                PostStatusService.PostStatusObserver observer = invocation.getArgument(1);
                observer.handleSuccessPostStatus("Successfully Posted!");
                return null;
            }
        };

        Mockito.doAnswer(callHandleSucceededAnswer).when(mockPostStatusService).postStatus(Mockito.any(), Mockito.any());

        // Invoke the postStatus
        mainPresenterSpy.postStatus("Hello there");

        // Verify that the expected methods were called on the main view
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayInfoMessage("Successfully Posted!");

        // Verify that the cache was cleared
        Mockito.verify(mockCache).clearCache();
    }

    @Test
    public void test_PostStatusFail() throws MalformedURLException, ParseException {
        // Create an answer object to make the mock UserService call handleFailureObserver on it's
        // observer when it's logout method is called
        Answer<Void> callHandleFailedAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals(invocation.getArgument(0), "Hello there");
                PostStatusService.PostStatusObserver observer = invocation.getArgument(1);
                observer.handleFailureObserver("failed to post message");
                return null;
            }
        };

        Mockito.doAnswer(callHandleFailedAnswer).when(mockPostStatusService).postStatus(Mockito.any(), Mockito.any());

        // Invoke the postStatus
        mainPresenterSpy.postStatus("Hello there");

        // Verify that the expected methods were called on the main view
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).clearInfoMessage();
        Mockito.verify(mockMainView).displayErrorMessage("Failed to: failed to post message");

        Mockito.verify(mockCache, Mockito.times(0)).clearCache();
    }

    @Test
    public void test_PostStatusException() throws MalformedURLException, ParseException {
        // Create an answer object to make the mock UserService call handleExceptionObserver on it's
        // observer when it's logout method is called
        Answer<Void> callHandleExceptionAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals(invocation.getArgument(0), "Hello there");
                PostStatusService.PostStatusObserver observer = invocation.getArgument(1);
                observer.handleExceptionObserver(new Exception("exception to post message"));
                return null;
            }
        };

        Mockito.doAnswer(callHandleExceptionAnswer).when(mockPostStatusService).postStatus(Mockito.any(), Mockito.any());

        // Invoke the postStatus
        mainPresenterSpy.postStatus("Hello there");

        // Verify that the expected methods were called on the main view
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).clearInfoMessage();
        Mockito.verify(mockMainView).displayErrorMessage("Failed because of exception: exception to post message");

        Mockito.verify(mockCache, Mockito.times(0)).clearCache();
    }
}
