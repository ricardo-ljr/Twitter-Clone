package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.FeedQueueMessage;
import edu.byu.cs.tweeter.server.dao.implementation.FollowDAO;
import edu.byu.cs.tweeter.server.util.JsonSerializer;

public class PostStatusQueueProcessor implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for(SQSEvent.SQSMessage msg : event.getRecords()) {

            Status status = JsonSerializer.deserialize(msg.getBody(), Status.class);

            List<User> followees = new FollowDAO().getFollowingQueue(status.getUser().getAlias());

            String message = JsonSerializer.serialize(new FeedQueueMessage(followees, status));
            String queueUrl = "https://sqs.us-west-1.amazonaws.com/587140454100/CS340FeedQueue";

            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(message);

            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);

        }

        return null;
    }
}
