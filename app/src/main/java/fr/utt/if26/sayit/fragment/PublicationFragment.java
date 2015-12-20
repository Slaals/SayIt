package fr.utt.if26.sayit.fragment;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.bean.Country;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

/**
 * Created by Jonathan on 20/12/2015.
 */
public class PublicationFragment extends Fragment {

    private MediaRecorder recorder = null;
    private File audioFile = null;

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mediaRecorder, int i, int i1) {
            //TODO
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
            //TODO
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();

        String accessToken = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE)
                .getString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, null);

        ItSaysEndpoints.PublicationEndpoints.publication(accessToken, bundle.getString("id"), getContext(), new ApiHttpClient.ApiCallFinished() {
            @Override
            public void onApiCallSucceeded(JSONObject response) {
                TextView publication = (TextView) getView().findViewById(R.id.publicationTextViewPublication);
                ImageView language = (ImageView) getView().findViewById(R.id.languageImageViewPublication);
                try {
                    JSONObject publicationObj = response.getJSONObject("publication");
                    Country country = Country.getByIsoCode(publicationObj.getString("language"));

                    publication.setText(publicationObj.getString("text"));
                    language.setImageDrawable(ContextCompat.getDrawable(getContext(), country.getDrawableResource()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onApiCallCompleted() {
            }

            @Override
            public void onApiCallFailed(JSONObject response) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publication, container, false);

        Button recordBtn = (Button)view.findViewById(R.id.recordButtonPublication);

        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            startRecording();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecording();
                        break;
                }

                return false;
            }
        });

        return view;
    }

    private void startRecording() throws IOException {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        File sampleDir = Environment.getExternalStorageDirectory();
        try {
            audioFile = File.createTempFile("ibm", ".3gp", sampleDir);
        } catch (IOException e) {
            Log.e("SoundRecording", "SDcard access error");
            return;
        }
        recorder.setOutputFile(audioFile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }

    private void stopRecording() {
        if(null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            sendRecordToAPI();

            recorder = null;
        }
    }

    private void sendRecordToAPI() {
        audioFile.getAbsoluteFile();
        Toast.makeText(getContext(), audioFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }
}
