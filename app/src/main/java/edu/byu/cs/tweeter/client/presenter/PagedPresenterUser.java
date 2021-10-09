package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PagedPresenterUser extends Presenter<User> {

    private PagedView view;

    public PagedPresenterUser(PagedView view,User user) {
        super(user);
        this.view = view;
    }

    protected PagedView getView() {
        return view;
    }

    public void handleFailure(String message) {
        view.displayErrorMessage("Failed: " + message);
    }

    public void handleException(Exception e) {
        view.displayErrorMessage("Failed: " + e.getMessage());
    }

    public void handleSuccessUser(User user) {
        view.navigateToUser(user);
    }

    @Override
    protected boolean loadItems() throws MalformedURLException {
        if (!getIsLoading() && isHasMorePages()) {
            setIsLoading(true);
            getView().setLoading(true);
            return true;
        } else {
            return false;
        }
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
