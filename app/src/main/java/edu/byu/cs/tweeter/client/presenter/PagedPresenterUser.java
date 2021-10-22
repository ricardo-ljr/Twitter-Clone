package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PagedPresenterUser extends Presenter<User> {

    public PagedPresenterUser(PagedView view,User user) {
        super(view, user);
    }

    @Override
    protected void handleSuccess(List<User> users, boolean hasMorePages, User lastFollower) throws MalformedURLException {
        getView().setLoading(false);
        getView().addItems(users);
        setHasMorePages(hasMorePages);
        setLastFollower(lastFollower);
        setIsLoading(false);
    }
}
