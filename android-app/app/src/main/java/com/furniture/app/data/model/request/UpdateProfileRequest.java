package com.furniture.app.data.model.request;

public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;

    public UpdateProfileRequest(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
}
