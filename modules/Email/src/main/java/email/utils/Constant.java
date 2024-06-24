package email.utils;

public class Constant {
    public static final String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    public static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String CLIENT_ID = "";
    public static final String CLIENT_SECRET = "";
    public static final String SCOPES = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/gmail.modify https://www.googleapis.com/auth/gmail.labels";

    public static final String REDIRECT_URI = "http://localhost:8080/api/gmail/access-token";
}
