package com.dankunlee.androidforumapp.request;

import androidx.annotation.NonNull;

public abstract class HttpRequest {
    private String baseHost;

    // HTTP Request Method
    private String requestMethod;

    // HTTP Request Header
    private String contentLanguage = "en-US";
    private String acceptCharest = "UTF-8";
    private String contentType = "application/json";
    private boolean doOutput = false;

    // HTTP Request Body
    private String jsonInput;

    public HttpRequest(String baseHost, String requestMethod, String jsonInput, String contentType, String contentLanguage, String acceptCharset) {
        this.baseHost = baseHost;
        this.requestMethod = requestMethod;
        if (requestMethod.equals("PUT") || requestMethod.equals("POST"))
            doOutput = true;
        this.jsonInput = jsonInput;

        if (contentType != null) this.contentType = contentType;
        if (contentLanguage != null) this.contentType = contentLanguage;
        if (acceptCharset != null) this.contentType = acceptCharset;
    }

    public abstract String generateFullRequestURL();

    // Getters and Setters

    public String getBaseHost() {
        return baseHost;
    }

    public void setBaseHost(String baseHost) {
        this.baseHost = baseHost;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    public String getAcceptCharest() {
        return acceptCharest;
    }

    public void setAcceptCharest(String acceptCharest) {
        this.acceptCharest = acceptCharest;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isDoOutput() {
        return doOutput;
    }

    public void setDoOutput(boolean doOutput) {
        this.doOutput = doOutput;
    }

    public String getJsonInput() {
        return jsonInput;
    }

    public void setJsonInput(String jsonInput) {
        this.jsonInput = jsonInput;
    }
}
