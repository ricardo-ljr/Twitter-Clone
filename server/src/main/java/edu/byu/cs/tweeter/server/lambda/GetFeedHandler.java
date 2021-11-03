package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.service.StatusService;

public class GetFeedHandler implements RequestHandler<FeedRequest, FeedResponse> {

    @Override
    public FeedResponse handleRequest(FeedRequest request, Context context) {
        StatusService service = new StatusService();
        FeedResponse response = service.getFeed(request);

        System.out.println("List of statuses:");

        for(Status st: response.getStatuses()) {
            System.out.println(st.toString());
        }

        return response;
    }


}
