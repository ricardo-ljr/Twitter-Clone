package edu.byu.cs.tweeter.client.presenter;

public interface ServiceView {
    void displayErrorMessage(String message);
    void clearErrorMessage();
    void displayInfoMessage(String message);
    void clearInfoMessage();
}
