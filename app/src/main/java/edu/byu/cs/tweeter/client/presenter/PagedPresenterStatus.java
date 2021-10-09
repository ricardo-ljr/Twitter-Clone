package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenterStatus extends Presenter<Status>{

    private PagedView view;

    protected PagedPresenterStatus(PagedView view, User user) {
        super(user);
        this.view = view;
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

    protected PagedView getView() {
        return view;
    }

    protected abstract void getUsers(String alias);

    @Override
    protected final boolean loadItems() throws MalformedURLException {
        if (!getIsLoading() && isHasMorePages()) {
            setIsLoading(true);
            getView().setLoading(true);
            return true;
         } else {
            return false;
        }
    }

    @Override
    protected void handleSuccess(List<Status> statuses, boolean hasMorePages, Status lastStatus) throws MalformedURLException {
        getView().setLoading(false);
        getView().addItems(statuses);
        setHasMorePages(hasMorePages);
        setLastStatus(lastStatus);
        setIsLoading(false);
    }
}
