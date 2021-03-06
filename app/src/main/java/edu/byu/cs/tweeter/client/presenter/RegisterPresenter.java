package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {
    @Override
    public void handleSuccess(User user, AuthToken authToken) {
        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello " + user.getName());
    }

    @Override
    public void handleFailureObserver(String message) {
        view.displayErrorMessage(message);
    }

    @Override
    public void handleExceptionObserver(Exception exception) {
        view.displayErrorMessage("Failed to register because of exception: " + exception.getMessage());
    }

    private RegisterPresenter.View view;

    public interface View extends ServiceView {
        void navigateToUser(User user);
        void uploadingImage();
    }

    public RegisterPresenter(View view) {
        this.view = view;
    }

    public void uploadImage() {
        view.uploadingImage();
    }

    public void register(String firstName, String lastName, String alias, String password, ImageView image) {
        view.clearErrorMessage();
        view.clearInfoMessage();

        String message = validateRegistration(firstName, lastName, alias, password, image);

        if (message == "Validated") {
            view.displayInfoMessage("Registering...");
            new UserService().register(firstName, lastName, alias, password, image, this);
        }
        else {
            view.displayErrorMessage("Failed to Register: " + message);
        }
    }

    private String validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (alias.length() == 0) {
            return ("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            return ("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            return ("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            return ("Password cannot be empty.");
        }

        if (imageToUpload.getDrawable() == null) {
            return ("Profile image must be uploaded.");
        }
        return "Validated";
    }

}
