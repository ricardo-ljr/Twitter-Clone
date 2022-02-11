package edu.byu.cs.tweeter.server.dao.interfaces;

import com.amazonaws.services.dynamodbv2.document.Item;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthTableDAOInterface {

    public void createSession(AuthToken token, String date);
    public boolean validateUser(AuthToken authToken);
    public void endSession(AuthToken token);
}
