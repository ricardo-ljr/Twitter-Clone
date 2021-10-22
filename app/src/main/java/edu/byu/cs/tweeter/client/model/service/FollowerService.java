package edu.byu.cs.tweeter.client.model.service;


import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserverStatus;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerService {

    private static final int PAGE_SIZE = 10;

    private final GetFollowersObserver observer;

    public interface GetFollowersObserver extends ServiceObserverStatus<User> { }

    public FollowerService(GetFollowersObserver observer) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(observer == null) {
            throw new NullPointerException();
        }

        this.observer = observer;
    }

    public void getFollowers(AuthToken authToken, User targetUser, User lastFollowee) {
        GetFollowersTask getFollowersTask = getGetFollowerTask(authToken, targetUser, PAGE_SIZE, lastFollowee);
        new Executor<>(getFollowersTask);
    }

    public GetFollowersTask getGetFollowerTask(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        return new GetFollowersTask(authToken, targetUser, limit, lastFollowee,
                new GetFollowersHandler(observer));
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends PagedServiceStatusUser {

        private GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver observer) {
            super((ServiceObserver) observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Follower Service";
        }

    }

}
