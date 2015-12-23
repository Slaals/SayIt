package fr.utt.if26.sayit.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jonathan on 21/12/2015.
 */
public class RecordAudio {

    private MediaRecorder recorder = null;
    private File audioFile = null;
    private boolean isRecording = false;

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

    public void startRecording() throws IOException {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        File sampleDir = Environment.getExternalStorageDirectory();
        try {
            audioFile = File.createTempFile("ibm", ".ogg", sampleDir);
        } catch (IOException e) {
            Log.e("SoundRecording", "SDcard access error");
            return;
        }
        recorder.setOutputFile(audioFile.getAbsolutePath());
        recorder.prepare();
        recorder.start();

        isRecording = true;
    }

    public void stopRecording() throws RuntimeException {
        if(null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;

            isRecording = false;
        }
    }

    public File getAudioFile() {
        return audioFile;
    }

    public boolean isRecording() {
        return isRecording;
    }
}
