package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.implementation.StoryDAO;
import edu.byu.cs.tweeter.server.dao.implementation.UserDAO;
import edu.byu.cs.tweeter.server.dao.interfaces.StoryDAOInterface;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.util.Pair;

public class StoryServiceTest {

    private StoryDAO mockStoryDao;
    private UserDAO mockUserDao;
    private StoryRequest storyRequest;
    private StoryResponse storyResponse;
    private DynamoDBDAOFactoryInterface mockFactory;

    private StatusService statusServiceSpy;
    private UserService userServiceSpy;

    private String imageUrl = "https://mycs340bucket.s3.us-west-1.amazonaws.com/%40a.jpg";
    @BeforeEach
    public void setup() {

        User currUser = new User("a", "a", "@a", imageUrl);
        List<String> urls = new ArrayList<>();
        List<String> mentions = new ArrayList<>();

        Status status1 = new Status("Hello there1", currUser, "TimeStamp",urls ,mentions);
        Status status2 = new Status("Hello there2", currUser, "TimeStamp",urls ,mentions);
        Status status3 = new Status("Hello there3", currUser, "TimeStamp",urls ,mentions);
        Status status4 = new Status("Hello there4", currUser, "TimeStamp",urls ,mentions);

        mockFactory = Mockito.spy(DynamoDBDAOFactoryInterface.class); // mock factory instance

        // Send in request
        storyRequest = new StoryRequest(new AuthToken(), "@a", 4, null); // Accessing @a alias

        storyResponse = new StoryResponse(Arrays.asList(status1, status2, status3, status4), false);

        mockStoryDao = Mockito.mock(StoryDAO.class); // mock storydao
        mockUserDao = Mockito.mock(UserDAO.class); // mock userdao

        Mockito.when(mockFactory.getStoryDAO()).thenReturn(mockStoryDao);
        Mockito.when(mockFactory.getUserDAO()).thenReturn(mockUserDao);

//        mockStoryDao.getStories(storyRequest);

        ItemCollection<QueryOutcome> stories = mockStoryDao.getStories(storyRequest); // new item collection - list of statuses from story
//
        Mockito.when(mockStoryDao.getStories(storyRequest)).thenReturn(stories);

        statusServiceSpy = Mockito.spy(new StatusService(mockFactory)); // spy service class
        userServiceSpy = Mockito.spy(new UserService(mockFactory)); // spy user service class

        Mockito.when(mockStoryDao.getStories(storyRequest)).thenReturn(stories);

        // Test service layer
        //

    }

    @Test
    public void userStoryPageTest() {

        StoryResponse response = statusServiceSpy.getStory(storyRequest);

        Assertions.assertEquals(response.getStatuses().size(), storyResponse.getStatuses().size()); // checking if the length is the same
        Assertions.assertEquals(response.getStatuses().get(0).getPost(), storyResponse.getStatuses().get(0).getPost());
        Assertions.assertEquals(response.getStatuses().get(0).getDate(), storyResponse.getStatuses().get(0).getDate());
        Assertions.assertEquals(response.getStatuses().get(1).getPost(), storyResponse.getStatuses().get(1).getPost());
        Assertions.assertEquals(response.getStatuses().get(2), storyResponse.getStatuses().get(2));
    }

}
