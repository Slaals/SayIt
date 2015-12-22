package fr.utt.if26.sayit.fragment;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
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
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.adapter.AudioListAdapter;
import fr.utt.if26.sayit.adapter.ExpressionListAdapter;
import fr.utt.if26.sayit.bean.AudioItem;
import fr.utt.if26.sayit.bean.Country;
import fr.utt.if26.sayit.bean.ExpressionItem;
import fr.utt.if26.sayit.utils.RecordAudio;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

/**
 * Created by Jonathan on 20/12/2015.
 */
public class PublicationFragment extends Fragment {

    private RecordAudio ra;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ra = new RecordAudio();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_publication, container, false);

        Bundle bundle = this.getArguments();
        final String publicationId = bundle.getString("id");

        String accessToken = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE)
                .getString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, null);

        ItSaysEndpoints.PublicationEndpoints.publication(accessToken, publicationId, getContext(), new ApiHttpClient.ApiCallFinished() {
            @Override
            public void onApiCallSucceeded(JSONObject response) {
                ArrayList<AudioItem> audioList = new ArrayList<>();
                TextView publication = (TextView) getView().findViewById(R.id.publicationTextViewPublication);
                ImageView language = (ImageView) getView().findViewById(R.id.languageImageViewPublication);
                try {
                    JSONObject publicationObj = response.getJSONObject("publication");
                    Country country = Country.getByIsoCode(publicationObj.getString("language"));

                    JSONArray audioArray = publicationObj.getJSONArray("audio");
                    for (int i = 0; i < audioArray.length(); i++) {
                        AudioItem audioItem = new AudioItem(
                                audioArray.get(i).toString()
                        );
                        audioList.add(audioItem);
                    }
                    ListView listView = (ListView) view.findViewById(R.id.audioListView);
                    listView.setAdapter(new AudioListAdapter(getActivity(), R.layout.audio_item, audioList));

                    publication.setText(publicationObj.getString("text"));
                    assert country != null;
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

        Button recordBtn = (Button)view.findViewById(R.id.recordButtonPublication);
        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view1, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            ra.startRecording();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        try {
                            ra.stopRecording();

                            ra.stopRecording();
                            File audioFile = ra.getAudioFile();
                            if(audioFile.exists()) {
                                String accessToken = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE)
                                        .getString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, null);

                                try {
                                    ItSaysEndpoints.AudioEndpoints.audio(accessToken, publicationId, audioFile, getContext(), new ApiHttpClient.ApiCallFinished() {
                                        @Override
                                        public void onApiCallCompleted() {
                                            // TODO
                                        }

                                        @Override
                                        public void onApiCallSucceeded(JSONObject jsonObjectSignUp) {
                                            // TODO
                                        }

                                        @Override
                                        public void onApiCallFailed(JSONObject response) {
                                            // TODO
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                audioFile.deleteOnExit();
                            }
                        } catch (RuntimeException re) {
                            re.printStackTrace();
                        }

                        break;
                }

                return false;
            }
        });

        return view;
    }
}
