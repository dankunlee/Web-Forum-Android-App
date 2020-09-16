package com.dankunlee.androidforumapp.request;

public class UserRequest  {
    private static final String logInURL = "/api/login";
    private static final String logOutURL = "/api/logout";
    private static final String registerURL = "/api/register";

    public static class LogIn extends HttpRequest{
        public LogIn(String baseHost, String jsonInput) {
            super(baseHost, "POST", jsonInput, null, null, null);
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + logInURL;
        }
    }

    public static class LogOut extends HttpRequest{
        public LogOut(String baseHost) {
            super(baseHost, "POST", null, null, null, null);
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + logOutURL;
        }
    }

    public static class Register extends HttpRequest{
        public Register(String baseHost, String jsonInput) {
            super(baseHost, "POST", jsonInput, null, null, null);
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + registerURL;
        }
    }
}
