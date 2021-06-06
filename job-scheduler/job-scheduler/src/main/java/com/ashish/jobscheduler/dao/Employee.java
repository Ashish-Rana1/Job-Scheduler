package com.ashish.jobscheduler.dao;

public class Employee {
    private String firstName;
    private String lastName;

    public Employee(){

    }

    public  Employee(String fName, String lName){
        this.firstName = fName;
        this.lastName = lName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "firstName: " + firstName + ", lastName: " + lastName;
    }

}
