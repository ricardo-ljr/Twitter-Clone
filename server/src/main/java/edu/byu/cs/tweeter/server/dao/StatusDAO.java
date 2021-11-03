package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.server.util.FakeData;

public class StatusDAO {

    // TODO: I can't get my lambda function to work, what should I do?
    public FeedResponse getFeed(FeedRequest request) {

        List<Status> allStatus = getDummyStatus();
        List<Status> responseStatus = new ArrayList<>();

        boolean hasMorePages = false;
        if (allStatus != null) {

                int statusIndex = getStatusStartingIndex(request.getLastStatus(), allStatus);

                for(int limitCounter = 0; statusIndex < allStatus.size() && limitCounter < request.getLimit(); statusIndex++, limitCounter++) {
                    responseStatus.add(allStatus.get(statusIndex));
                }

                hasMorePages = statusIndex < allStatus.size();
            }


        return new FeedResponse(responseStatus, hasMorePages);


    }



    private int getStatusStartingIndex(Status lastStatus, List<Status> allStatus) {

        int statusIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatus.size(); i++) {
                if(lastStatus.getUser().getAlias().equals(allStatus.get(i).getUser().getAlias()) && lastStatus.getDate().equals(allStatus.get(i).getDate())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusIndex = i + 1;
                    break;
                }
            }
        }

        return statusIndex;
    }

    List<Status> getDummyStatus() {
        return getFakeData().getFakeStatuses();
    }


    FakeData getFakeData() {
        return new FakeData();
    }
}
