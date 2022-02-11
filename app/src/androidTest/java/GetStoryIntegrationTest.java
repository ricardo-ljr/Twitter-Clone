import android.os.Looper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class GetStoryIntegrationTest {

    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private static final String URL_PATH = "/getstory";

    private StoryRequest request;
    private StoryResponse response;
    private ServerFacade serverFacade;

    private StoryPresenter.View mockStoryView;
    private StoryService storyServiceSpy;
    private Cache mockCache;

    private StoryPresenter storyPresenterSpy;

    private User user1;
    private User user2;
    private AuthToken authToken;

    private final FakeData fakeData = new FakeData();

    private GetStoryTask validRequest_GetStoryTaskSpy;

    private GetStoryServiceObserver observer;

    private List<Status> successResponse_statuses;
    private boolean successResponse_hasMorePages;
    private int validRequest_limit;
    private Status validRequest_lastStatus;

    private CountDownLatch countDownLatch;

    @Before
    public void setup() throws IOException, TweeterRemoteException {

        user1 = new User("Allen", "Anderson", MALE_IMAGE_URL);
        user2 = new User("Bob", "Bobson", "@bob", MALE_IMAGE_URL);

        String url = "https://byu.edu";
        List<String> urls = Arrays.asList(url);
        List<String> mentions1 = Arrays.asList(user1.getAlias());
        List<String> mentions2 = Arrays.asList(user2.getAlias());

        Status resultStatus1 = new Status("Hello", user1,"time", urls, mentions2 );
        Status resultStatus2 = new Status("Hello2", user2,"time2", urls, mentions1 );
        Status resultStatus3 = new Status("Hello3", user2,"time3", urls, mentions1 );

        // Setup request data to use in tests

        authToken = new AuthToken();

        validRequest_limit = 10;
        validRequest_lastStatus = null;

        Pair<List<Status>, Boolean> pageOfItems = fakeData.getPageOfStatus(validRequest_lastStatus, validRequest_limit); // TODO: How to match timestamp?

        // Setup reponse data to use in tests

        successResponse_statuses = pageOfItems.getFirst();
        successResponse_hasMorePages = pageOfItems.getSecond();

        // Setup observer for StoryService

        observer = new GetStoryServiceObserver();
        resetCountDownLatch();

        // Create a StoryService instance and wrap it with a spy that will use mock tasks
        StoryService storyService = new StoryService(observer);
        storyServiceSpy = Mockito.spy(storyService);

        GetStoryTask validRequest_getStoryTask = new GetStoryTask(authToken, user1, validRequest_limit,validRequest_lastStatus,
                new StoryService.GetStoryHandler(Looper.getMainLooper(), observer));

        validRequest_GetStoryTaskSpy = Mockito.spy(validRequest_getStoryTask);

        Pair<List<Status>, Boolean> successResponse_statuses = new Pair<>(this.successResponse_statuses, successResponse_hasMorePages);

        Mockito.when(validRequest_GetStoryTaskSpy.getItems()).thenReturn(successResponse_statuses); //TODO: Double check later

        Mockito.when(storyServiceSpy.getGetStoryTask(authToken, user1, validRequest_limit, validRequest_lastStatus)).thenReturn(validRequest_GetStoryTaskSpy);

    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    // A {@link StoryService.GetStoryObserver} implementation
    private class GetStoryServiceObserver implements StoryService.GetStoryObserver {

        private List<Status> statuses;
        private boolean hasMorePages;
        private Status lastStatus;

        @Override
        public void handleSuccessStatus(List<Status> statuses, boolean hasMorePages, Status lastStatus) throws MalformedURLException {
            this.statuses = statuses;
            this.hasMorePages = hasMorePages;
            this.lastStatus = lastStatus;

            countDownLatch.countDown();
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean isHasMorePages() {
            return hasMorePages;
        }

        public Status getLastStatus() {
            return lastStatus;
        }

        @Override
        public void handleFailureObserver(String message) {

        }

        @Override
        public void handleExceptionObserver(Exception exception) {

        }
    }


    @Test
    public void testGetStory_validRequest_passesCorrectResponseToObserver() throws InterruptedException {

        storyServiceSpy.getStory(observer, user1, validRequest_lastStatus);

        awaitCountDownLatch();

        Assert.assertEquals(successResponse_statuses.get(0).getUser(), observer.getStatuses().get(0).getUser());
        Assert.assertEquals(successResponse_statuses.get(1).getUser(), observer.getStatuses().get(1).getUser());
        Assert.assertEquals(successResponse_statuses.get(2).getUser(), observer.getStatuses().get(2).getUser());
        Assert.assertEquals(successResponse_statuses.get(3).getUser(), observer.getStatuses().get(3).getUser());
        Assert.assertEquals(successResponse_statuses.get(4).getUser(), observer.getStatuses().get(4).getUser());
        Assert.assertEquals(successResponse_statuses.get(5).getUser(), observer.getStatuses().get(5).getUser());
        Assert.assertEquals(successResponse_statuses.get(6).getUser(), observer.getStatuses().get(6).getUser());
        Assert.assertEquals(successResponse_statuses.get(0).getPost(), observer.getStatuses().get(0).getPost());
        Assert.assertEquals(successResponse_statuses.get(1).getPost(), observer.getStatuses().get(1).getPost());
        Assert.assertEquals(successResponse_statuses.get(2).getPost(), observer.getStatuses().get(2).getPost());
        Assert.assertEquals(successResponse_statuses.get(3).getPost(), observer.getStatuses().get(3).getPost());
        Assert.assertEquals(successResponse_statuses.get(4).getPost(), observer.getStatuses().get(4).getPost());
        Assert.assertEquals(successResponse_statuses.get(5).getPost(), observer.getStatuses().get(5).getPost());
        Assert.assertEquals(successResponse_statuses.get(6).getPost(), observer.getStatuses().get(6).getPost());
        Assert.assertEquals(successResponse_statuses.size(), observer.getStatuses().size());
        Assert.assertEquals(successResponse_hasMorePages, observer.isHasMorePages());
    }


}
