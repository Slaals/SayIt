package fr.utt.if26.itsaysclient;

public class ItSaysEndpoints extends ApiHttpClient {

    /*
    All endpoint method have a last parameter used as a callback executed when the HTTP request is finished.
    By Passing the callback instance (which implements onApiCallFinished()) to the HTTP Client, it
    can automatically call onApiCallFinished() when the HTTP request is finished.
    */

    public static class UserEndpoint {

        public static void signup(String username, String password) {
            // TODO : do the signup call to the ItSays "/signup" endpoint
        }

        public static void signin(String username, String password, ApiCallFinished callback) {
            ApiHttpClient client = new ApiHttpClient();
            client.setRelativeEndpointPath("/signin");
            client.setHttpMethod(EnumHttpMethod.GET);
            client.setBodyParamType(EnumBodyParamType.X_WWW_FORM_URLENCODED);
            client.addQueryParam("username", username);
            client.addQueryParam("password", password);
            client.launchApiCall(callback);
        }
    }

    public static class PublicationEndpoint {

        public static void publications(String accessToken, ApiCallFinished callback) {
            ApiHttpClient client = new ApiHttpClient();
            client.setRelativeEndpointPath("/publications");
            client.setHttpMethod(EnumHttpMethod.GET);
            client.addHeaderParam("x-access-token", accessToken);
            client.launchApiCall(callback);
        }
    }
}
