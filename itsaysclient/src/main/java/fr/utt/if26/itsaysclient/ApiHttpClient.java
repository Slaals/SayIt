package fr.utt.if26.itsaysclient;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/*
The three AsyncTask generics are respectively matching to :
- First : the PARAMETER type of doInBackground()
- Second : the PARAMETER type of onProgressUpdate()
- Third : the RETURN type of doInBackground()
*/
public class ApiHttpClient extends AsyncTask<Void, Void, JSONObject> {

    private static final String API_ROOT_LINK = "http://itsays.pablo-prudhommeau.com:8127/api";

    private static final int CONNECTION_TIMEOUT = 5000;

    private EnumHttpMethod httpMethod;
    private EnumBodyParamType bodyParamType;

    private HashMap<String, String> bodyParams;
    private HashMap<String, String> queryParams;
    private HashMap<String, String> headerParams;
    private HashMap<String, File> filesParams;
    private String relativeEndpointPath;

    private final String boundary;
    private final String LINE_FEED = "\r\n";

    private Context context;

    public ApiHttpClient() {
        bodyParams = new HashMap<>();
        queryParams = new HashMap<>();
        headerParams = new HashMap<>();
        filesParams = new HashMap<>();

        boundary = "****" + System.currentTimeMillis();

        bodyParamType = EnumBodyParamType.DEFAULT;
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

    @TargetApi(23)
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
            switch (bodyParamType) {
                case X_WWW_FORM_URLENCODED:
                    OutputStream outStream = urlConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outStream));
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

                    bufferedWriter.close();
                    outStream.close();

                    break;
                case FORM_DATA:
                    outStream = urlConnection.getOutputStream();
                    PrintWriter writer = new PrintWriter(outStream);

                    File audioFile = filesParams.get("audio");

                    writer.append("--")
                            .append(boundary)
                            .append(LINE_FEED);

                    writer.append("Content-Disposition: form-data; name=\"audio\"; filename=\"")
                            .append(audioFile.getName())
                            .append("\"")
                            .append(LINE_FEED);

                    writer.append("Content-Type: ")
                            .append(URLConnection.guessContentTypeFromName(audioFile.getName()))
                            .append(LINE_FEED);

                    writer.append("Content-Transfer-Encoding: binary")
                            .append(LINE_FEED);

                    writer.append(LINE_FEED);
                    writer.flush();

                    FileInputStream inputStream = new FileInputStream(audioFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                    outStream.flush();
                    inputStream.close();

                    writer.append(LINE_FEED);

                    writer.append(LINE_FEED).flush();
                    writer.append("--")
                            .append(boundary)
                            .append("--")
                            .append(LINE_FEED);

                    writer.close();

                    break;
                case DEFAULT:
                    break;
            }


            // Not the best way, but have to go ahead
            if(urlConnection.getContentType().compareTo("application/ogg") == 0) {
                File sampleDir = Environment.getExternalStorageDirectory();

                File file = File.createTempFile("mbi", ".ogg", sampleDir);
                file.deleteOnExit();

                // write the inputStream to a FileOutputStream
                OutputStream outputStream =
                        new FileOutputStream(file);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = urlConnection.getInputStream().read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                response = new JSONObject();
                response.put("success", true);
                response.put("file", file.getAbsolutePath());
            } else {
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
            }

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
                Toast toast = Toast.makeText(context, context.getResources().getString(R.string.apiCommunicationError) + response.getString("http_error"), Toast.LENGTH_LONG);
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

    public void addFilePart(String key, File value) throws IOException { filesParams.put(key, value); }

    public void setHttpMethod(EnumHttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setRelativeEndpointPath(String relativeEndpointPath) {
        this.relativeEndpointPath = relativeEndpointPath;
    }

    public String getBoundary() {
        return boundary;
    }
    /* ------------------- */
}
