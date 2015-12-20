package fr.utt.if26.itsaysclient;

import android.content.Context;

public class ItSaysEndpoints extends ApiHttpClient {

    /*
    All endpoint method have a last parameter used as a callback executed when the HTTP request is finished.
    By Passing the callback instance (which implements onApiCallSucceeded()) to the HTTP Client, it
    can automatically call onApiCallSucceeded() when the HTTP request is finished.
    */

    public static class UserEndpoints {

        public static void signup(String username, String password, Context context, ApiCallFinished callback) {
            ApiHttpClient client = new ApiHttpClient();
            client.setRelativeEndpointPath("/signup");
            client.setHttpMethod(EnumHttpMethod.POST);
            client.setBodyParamType(EnumBodyParamType.X_WWW_FORM_URLENCODED);
            client.addBodyParam("username", username);
            client.addBodyParam("password", password);
            client.launchApiCall(context, callback);
        }

        public static void signin(String username, String password, Context context, ApiCallFinished callback) {
            ApiHttpClient client = new ApiHttpClient();
            client.setRelativeEndpointPath("/signin");
            client.setHttpMethod(EnumHttpMethod.GET);
            client.addQueryParam("username", username);
            client.addQueryParam("password", password);
            client.launchApiCall(context, callback);
        }

        public static void logout(String accessToken, Context context, ApiCallFinished callback) {
            ApiHttpClient client = new ApiHttpClient();
            client.setRelativeEndpointPath("/logout");
            client.setHttpMethod(EnumHttpMethod.DELETE);
            client.addHeaderParam("x-access-token", accessToken);
            client.launchApiCall(context, callback);
        }
    }

    public static class PublicationEndpoints {

        public static void publications(String accessToken, Context context, ApiCallFinished callback) {
            ApiHttpClient client = new ApiHttpClient();
            client.setRelativeEndpointPath("/publications");
            client.setHttpMethod(EnumHttpMethod.GET);
            client.addHeaderParam("x-access-token", accessToken);
            client.launchApiCall(context, callback);
        }

        public static void publication(String accessToken, String text, String language, Context context, ApiCallFinished callback) {
            ApiHttpClient client = new ApiHttpClient();
            client.setRelativeEndpointPath("/publication");
            client.setHttpMethod(EnumHttpMethod.POST);
            client.addHeaderParam("x-access-token", accessToken);
            client.setBodyParamType(EnumBodyParamType.X_WWW_FORM_URLENCODED);
            client.addBodyParam("text", text);
            client.addBodyParam("language", language);
            client.launchApiCall(context, callback);
        }

        public static void publication(String accessToken, String id, Context context, ApiCallFinished callback) {
            ApiHttpClient client = new ApiHttpClient();
            client.setRelativeEndpointPath("/publication/" + id);
            client.setHttpMethod(EnumHttpMethod.GET);
            client.addHeaderParam("x-access-token", accessToken);
            client.launchApiCall(context, callback);
        }
    }
}
