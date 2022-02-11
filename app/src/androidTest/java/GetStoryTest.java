import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.PostStatusService;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class GetStoryTest {

    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private static final String URL_PATH = "/getstory";

    private StoryRequest request;
    private StoryResponse response;
    private ServerFacade serverFacade;

    private StoryPresenter.View mockStoryView;
    private StoryService mockStoryService;
    private Cache mockCache;

    private StoryPresenter storyPresenterSpy;

    private final User user1 = new User("Allen", "Anderson", MALE_IMAGE_URL);
    private final AuthToken fakeAuthToken = new AuthToken();

    private final FakeData fakeData = new FakeData();

    private List<Status> statuses;
    private boolean hasMorePages;


    @Before
    public void setup() throws IOException, TweeterRemoteException {
        // Create mock StoryPresenter dependencies
        mockStoryView = Mockito.mock(StoryPresenter.View.class);
        mockStoryService = Mockito.mock(StoryService.class);
        mockCache = Mockito.mock(Cache.class);


        request = new StoryRequest(fakeAuthToken, user1.getAlias(), 10, null);
        Pair<List<Status>, Boolean> pageOfItems = fakeData.getPageOfStatus(request.getLastStatus(), request.getLimit());
        statuses = pageOfItems.getFirst();
        hasMorePages = pageOfItems.getSecond();
        response = new StoryResponse(statuses, hasMorePages);

        // Create a story presenter spy so we can mock it with the userServiceMock

        storyPresenterSpy = Mockito.spy(new StoryPresenter(mockStoryView, user1));

        serverFacade = Mockito.spy(new ServerFacade());

        Mockito.when(serverFacade.getStory(request, URL_PATH)).thenReturn(response);
        Mockito.doReturn(mockStoryService).when(storyPresenterSpy).getStoryService(Mockito.any());

        // Inject a mock cache into the Cache class
        Cache.setInstance(mockCache);

    }

    @Test
    public void test_GetStorySuccess() throws MalformedURLException, ParseException {
        // Create an answer object to make the mock UserService call handleSucceeded on it's
        // observer when it's logout method is called
        Answer<Void> callHandleSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StoryService.GetStoryObserver observer = invocation.getArgument(0);
                Status lastStatus = invocation.getArgument(2);
                observer.handleSuccessStatus(statuses, hasMorePages, request.getLastStatus());
                return null;
            }
        };

        Mockito.doAnswer(callHandleSucceededAnswer).when(mockStoryService).getStory(Mockito.any(), Mockito.any(), Mockito.any());

        // Invoke the loadMoreItems
        storyPresenterSpy.loadMoreItems(); // Test the actual service, integration test, only mock observer
        // Verify that the mockObserver was called correctly, Looper()
        // countDownLatch

        // Verify that the expected methods were called on the main view
        Mockito.verify(mockStoryView).setLoading(true);
        Mockito.verify(mockStoryView).setLoading(false);
        Mockito.verify(mockStoryView).addItems(statuses);

        // Verify that the cache was not cleared
        Mockito.verify(mockCache, Mockito.times(0)).clearCache();
    }

}
