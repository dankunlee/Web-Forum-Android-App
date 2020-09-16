package com.dankunlee.androidforumapp.request;

public class CommentRequest {
    private static final String getAllCommentsURL = "/api/post";

    public static class GetAllComments extends HttpRequest {
        String postID;
        public GetAllComments(String baseHost, String postID) {
            super(baseHost, "GET", null, null, null, null);
            this.postID = postID;
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + getAllCommentsURL + "/" + postID + "/comment";
        }
    }

    public static class WriteComment extends HttpRequest {
        String postID;
        public WriteComment(String baseHost, String postID, String jsonInput) {
            super(baseHost, "PUT", jsonInput, null, null, null);
            this.postID = postID;
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + getAllCommentsURL + "/" + postID + "/comment";
        }
    }

    public static class DeleteComment extends HttpRequest {
        String postID, commentID;
        public DeleteComment(String baseHost, String postID, String commentID) {
            super(baseHost, "DELETE", null, null, null, null);
            this.postID = postID;
            this.commentID = commentID;
        }

        @Override
        public String generateFullRequestURL() {
            return getBaseHost() + getAllCommentsURL + "/" + postID + "/comment/" + commentID;
        }
    }

    // TODO: UpdateComment
}
