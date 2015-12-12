package fr.utt.if26.itsaysclient;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/*
The three AsyncTask generics are respectively matching to :
- First : the PARAMETER type of doInBackground()
- Second : the PARAMETER type of onProgressUpdate()
- Third : the RETURN type of doInBackground()
*/
public class ApiHttpClient extends AsyncTask<Void, Void, JSONObject> {

    // TODO : change the production API root link (It currently point to a network dependant development machine)
    private static final String apiRootLink = "http://192.168.1.40:8080/api";

    private EnumHttpMethod httpMethod;
    private EnumBodyParamType bodyParamType;

    private HashMap<String, String> bodyParams;
    private HashMap<String, String> queryParams;
    private String relativeEndpointPath;

    public ApiHttpClient() {
        bodyParams = new HashMap<>();
        queryParams = new HashMap<>();
    }

    public void setBodyParamType(EnumBodyParamType bodyParamType) {
        this.bodyParamType = bodyParamType;
    }

    /*
    This interface is crucial for handle the asynchronous way of HTTP requests
    Basically, each endpoint defined in ItSaysEndpoints require an instance inheriting
    of ApiCallFinished. In this way, we can call onApiCallFinished() on this instance
    when the http request is finished. This basically acting like a callback.
    */
    public interface ApiCallFinished {
        void onApiCallFinished(JSONObject response);
    }

    public ApiCallFinished callback = null;

    public AsyncTask launchApiCall(ApiCallFinished callback) {
        this.callback = callback;
        // Launch the AsyncTask (invoking doInBackground() method first)
        return execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        StringBuilder urlString = new StringBuilder();
        urlString.append(apiRootLink);
        urlString.append(relativeEndpointPath);
        urlString.append("/");
        int i = 0;
        for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
            if (i == 0) {
                urlString.append("?");
            } else {
                urlString.append("&");
            }
            urlString.append(queryParam.getKey());
            urlString.append("=");
            urlString.append(queryParam.getValue());
            i++;
        }

        HttpURLConnection urlConnection;
        URL url;
        JSONObject response = null;

        try {
            url = new URL(urlString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(httpMethod.name());
            urlConnection.connect();

            // DEBUG : System.out.println("Call to ItSays API : " + urlString.toString() + " - HTTP/" + urlConnection.getResponseCode() + " (" + urlConnection.getResponseMessage() + ")");

            /* Initialize output stream for write the HTTP request body */
            if (!bodyParams.isEmpty()) {
                OutputStream outStream = urlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outStream));
                switch (bodyParamType) {
                    case X_WWW_FORM_URLENCODED:
                        for (Map.Entry<String, String> bodyParam : bodyParams.entrySet()) {
                            bufferedWriter.write(bodyParam.getKey() + "=" + bodyParam.getValue());
                        }
                        break;
                }
                bufferedWriter.close();
                outStream.close();
            }

            /* Initialize input stream for read the HTTP response body */
            InputStream inStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
            String bufferedResponse, responseString = "";
            while ((bufferedResponse = bufferedReader.readLine()) != null) {
                responseString += bufferedResponse;
            }
            response = new JSONObject(responseString);
            bufferedReader.close();
            inStream.close();

            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(JSONObject response) {
        callback.onApiCallFinished(response);
        super.onPostExecute(response);
    }

    /* Setters and getters */
    /* ------------------- */
    public void addQueryParam(String key, String value) {
        queryParams.put(key, value);
    }

    public void addBodyParam(String key, String value) {
        bodyParams.put(key, value);
    }

    public void setHttpMethod(EnumHttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setRelativeEndpointPath(String relativeEndpointPath) {
        this.relativeEndpointPath = relativeEndpointPath;
    }
    /* ------------------- */
}
