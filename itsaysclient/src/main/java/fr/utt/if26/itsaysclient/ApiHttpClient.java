package fr.utt.if26.itsaysclient;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
    private static final String API_ROOT_LINK = "http://192.168.1.85:8080/api";

    private static final int CONNECTION_TIMEOUT = 5000;

    private EnumHttpMethod httpMethod;
    private EnumBodyParamType bodyParamType;

    private HashMap<String, String> bodyParams;
    private HashMap<String, String> queryParams;
    private HashMap<String, String> headerParams;
    private String relativeEndpointPath;

    private Context context;

    public ApiHttpClient() {
        bodyParams = new HashMap<>();
        queryParams = new HashMap<>();
        headerParams = new HashMap<>();
    }

    public void setBodyParamType(EnumBodyParamType bodyParamType) {
        this.bodyParamType = bodyParamType;
    }

    /*
    This interface is crucial for handle the asynchronous way of HTTP requests
    Basically, each endpoint defined in ItSaysEndpoints require an instance inheriting
    of ApiCallFinished. In this way, we can call onApiCallSucceeded() on this instance
    when the http request is finished. This basically acting like a callback.
    */
    public interface ApiCallFinished {
        void onApiCallCompleted();

        void onApiCallSucceeded(JSONObject response);

        void onApiCallFailed(JSONObject response);
    }

    public ApiCallFinished callback = null;

    public AsyncTask launchApiCall(Context context, ApiCallFinished callback) {
        this.context = context;
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
        urlString.append(API_ROOT_LINK);
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
        String error = null;

        try {
            url = new URL(urlString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(CONNECTION_TIMEOUT);

            urlConnection.setRequestMethod(httpMethod.name());

            for (Map.Entry<String, String> header : headerParams.entrySet()) {
                urlConnection.setRequestProperty(header.getKey(), header.getValue());
            }

            urlConnection.connect();

            /* Initialize output stream for write the HTTP request body */
            if (!bodyParams.isEmpty()) {
                OutputStream outStream = urlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outStream));
                switch (bodyParamType) {
                    case X_WWW_FORM_URLENCODED:
                        int j = 0;
                        for (Map.Entry<String, String> bodyParam : bodyParams.entrySet()) {
                            StringBuilder stringToWrite = new StringBuilder();
                            stringToWrite.append(bodyParam.getKey());
                            stringToWrite.append("=");
                            stringToWrite.append(bodyParam.getValue());
                            if (j < bodyParams.size()) {
                                stringToWrite.append("&");
                            }
                            bufferedWriter.write(stringToWrite.toString());
                            j++;
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

            // DEBUG
            System.out.println("Call to ItSays API : " + urlString.toString() + " - HTTP/" + urlConnection.getResponseCode() + " (" + urlConnection.getResponseMessage() + ")");

            urlConnection.disconnect();
        } catch (IOException e) {
            error = e.getLocalizedMessage();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Si an IO error during HTTP request had been catch
        if (response == null) {
            try {
                JSONObject tempResponse = new JSONObject();
                tempResponse.put("success", false);
                if (error != null) {
                    tempResponse.put("http_error", error);
                }
                response = tempResponse;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(JSONObject response) {
        callback.onApiCallCompleted();
        try {
            if (response.has("http_error")) {
                Toast toast = Toast.makeText(context, context.getResources().getString(R.string.apiCommunicationError) + " : " + response.getString("http_error"), Toast.LENGTH_LONG);
                toast.show();
            } else if (response.has("success")) {
                if (response.getBoolean("success")) {
                    callback.onApiCallSucceeded(response);
                } else {
                    String errorMessage;
                    System.out.println(response.getString("errorCode"));
                    if (context.getResources().getIdentifier(response.getString("errorCode"), "string", context.getPackageName()) != 0) {
                        errorMessage = context.getResources().getString(context.getResources().getIdentifier(response.getString("errorCode"), "string", context.getPackageName()));
                    } else {
                        errorMessage = context.getResources().getString(R.string.unknownItSaysApiErrorCode) + response.getString("errorCode");
                    }
                    Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
                    toast.show();
                    callback.onApiCallFailed(response);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(response);
    }

    /* Setters and getters */
    /* ------------------- */
    public void addQueryParam(String key, String value) {
        queryParams.put(key, value);
    }

    public void addHeaderParam(String key, String value) {
        headerParams.put(key, value);
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
