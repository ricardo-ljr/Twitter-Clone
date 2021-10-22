package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenterStatus extends Presenter<Status>{

    protected PagedPresenterStatus(PagedView view, User user) {
        super(view, user);
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
