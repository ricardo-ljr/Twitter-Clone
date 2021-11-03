package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;

public class StatusService {

    public FeedResponse getFeed(FeedRequest request) {
        System.out.println("Getting the Feed: ");
        Pair<List<Status>, Boolean> pageOfItems = getFakeData().getPageOfStatus(request.getLastStatus(), request.getLimit());
        List<Status> statuses = pageOfItems.getFirst();
        boolean hasMorePages = pageOfItems.getSecond();


        System.out.println("HasMorePages:" + hasMorePages);

        System.out.println("Returning Response: ");
        return new FeedResponse(statuses, hasMorePages);
    }

    public StoryResponse getStory(StoryRequest request) {
        Pair<List<Status>, Boolean> pageOfItems = getFakeData().getPageOfStatus(request.getLastStatus(), request.getLimit());
        List<Status> statuses = pageOfItems.getFirst();
        boolean hasMorePages = pageOfItems.getSecond();

        return new StoryResponse(statuses, hasMorePages);
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        return new PostStatusResponse(request.getStatus());
    }


    StatusDAO getFeedDao() {
        return new StatusDAO();
    }

    FakeData getFakeData() {
        return new FakeData();
    }
}
