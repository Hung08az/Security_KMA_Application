package com.example.kma_application.Models;

public class ResponseModel {
    private String response;
    private Boolean res;
    private String role;
    private String token;

    public ResponseModel(String response, Boolean res, String role, String token) {
        this.response = response;
        this.res = res;
        this.role = role;
        this.token = token;
    }

    public ResponseModel(String response, Boolean res) {
        this.response = response;
        this.res = res;
    }

    public ResponseModel(String response, Boolean res, String role) {
        this.response = response;
        this.res = res;
        this.role = role;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Boolean getRes() {
        return res;
    }

    public void setRes(Boolean res) {
        this.res = res;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
