package fr.utt.if26.sayit.adapter;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.util.List;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.bean.AudioItem;
import fr.utt.if26.sayit.utils.AudioUtilities;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

/**
 * Created by Jonathan on 21/12/2015.
 */
public class AudioListAdapter extends ArrayAdapter<AudioItem> {

    Context mContext;

    int layoutResourceId;

    List<AudioItem> data = null;

    private MediaPlayer player = new MediaPlayer();
    private Handler handler = new Handler();

    private String currentPlay;
    private ProgressBar currentProgressBar;
    private TextView currentAudioDuration;

    public AudioListAdapter(Context mContext, int layoutResourceId, List<AudioItem> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        final AudioItem audioItem = data.get(position);

        final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.audioProgressBar);
        final TextView audioDuration = (TextView)convertView.findViewById(R.id.audioDuration);

        progressBar.setProgress(0);
        progressBar.setMax(100);

        final Button playBtn = (Button)convertView.findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentProgressBar = progressBar;
                currentAudioDuration = audioDuration;

                if (null == audioItem.getFile()) {
                    String accessToken = getContext().getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE)
                            .getString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, null);

                    ItSaysEndpoints.AudioEndpoints.audio(accessToken, audioItem.getId(), getContext(), new ApiHttpClient.ApiCallFinished() {
                        @Override
                        public void onApiCallCompleted() {

                        }

                        @Override
                        public void onApiCallSucceeded(JSONObject response) {
                            try {
                                audioItem.setFile(new File(response.getString("file")));
                                play(audioItem.getFile(), audioItem.getId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onApiCallFailed(JSONObject response) {

                        }
                    });
                } else {
                    if (currentPlay.compareTo(audioItem.getId()) == 0) {
                        if (player.isPlaying()) {
                            pause();
                        } else {
                            unpause();
                        }
                    } else {
                        play(audioItem.getFile(), audioItem.getId());
                    }
                }
            }
        });


        return convertView;
    }

    private void updateProgressBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            if(player.isPlaying()) {
                long totalDuration = player.getDuration();
                long currentDuration = player.getCurrentPosition();

                currentAudioDuration.setText(AudioUtilities.milliSecondsToTimer(player.getCurrentPosition()) + "/" +
                        AudioUtilities.milliSecondsToTimer(player.getDuration()));

                int progress = (int)(AudioUtilities.getProgressPercentage(currentDuration, totalDuration));

                currentProgressBar.setProgress(progress);

                handler.postDelayed(this, 100);
            } else {
                handler.removeCallbacks(updateTimeTask, null);
            }
        }
    };

    private void play(File file, String id) {
        player.stop();
        player = MediaPlayer.create(getContext(), Uri.fromFile(file));
        player.start();

        updateProgressBar();

        currentPlay = id;
    }

    private void unpause() {
        player.start();
    }

    private void pause() {
        player.pause();
    }

}
