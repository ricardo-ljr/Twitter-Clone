package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.DataAccessException;

public interface S3DAOInterface {

    public String upload(String alias, String image) throws DataAccessException;
}
