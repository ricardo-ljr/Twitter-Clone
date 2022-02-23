package edu.byu.cs.tweeter.model.net.request;

import javax.swing.text.html.ImageView;

public class RegisterRequest {

    private String alias;
    private String password;
    private String firstName;
    private String lastName;
    private String imageToUpload;

    private RegisterRequest() {}

    public RegisterRequest(String alias, String password, String firstName, String lastName, String imageToUpload) {
        this.alias = alias;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageToUpload = imageToUpload;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageToUpload() {
        return imageToUpload;
    }

    public void setImageToUpload(String imageToUpload) {
        this.imageToUpload = imageToUpload;
    }
}
