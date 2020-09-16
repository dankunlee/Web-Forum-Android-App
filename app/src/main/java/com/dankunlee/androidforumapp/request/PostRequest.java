package com.dankunlee.androidforumapp.request;

public class PostRequest {
    private static final String getAllPostsURL = "/api/post";

    public static class GetAllPosts extends HttpRequest{
        public GetAllPosts(String baseHost) {
            super(baseHost, "GET", null, null, null, null);
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + getAllPostsURL;
        }
    }

    public static class GetPage extends HttpRequest {
        private String getPageURL = null;

        public GetPage(String baseHost, int page, int pageSize) {
            super(baseHost, "GET", null, null, null, null);
            getPageURL =  getAllPostsURL + "/page?page=" + page + "&size=" + pageSize;
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + getPageURL;
        }
    }

    public static class GetPost extends HttpRequest {
        private String postID;

        public GetPost(String baseHost, String ID) {
            super(baseHost, "GET", null, null, null, null);
            postID = ID;
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + getAllPostsURL + "/" + postID;
        }
    }

    public static class WritePost extends HttpRequest {
        public WritePost(String baseHost, String jsonInput) {
            super(baseHost, "PUT", jsonInput, null, null, null);
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + getAllPostsURL;
        }
    }

    public static class DeletePost extends HttpRequest {
        String postID;
        public DeletePost(String baseHost, String postID) {
            super(baseHost, "DELETE", null, null, null, null);
            this.postID = postID;
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + getAllPostsURL + "/" + postID;
        }
    }
    // TODO: UpdatePost, DeletePost, UploadFile, DelteFile
}
