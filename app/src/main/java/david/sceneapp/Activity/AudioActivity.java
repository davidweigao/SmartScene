package david.sceneapp.Activity;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import david.sceneapp.R;


public class AudioActivity extends ActionBarActivity implements View.OnClickListener {

    private AudioManager mAudioManager;
    private TextView infoTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        infoTv = (TextView) findViewById(R.id.infoTV);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.audio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings: return true;
            case R.id.action_audio_mode:showAudioMode(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAudioMode() {
        int alarmVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int dtmfVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_DTMF);
        int musicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int notifVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        int systemVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        int ringVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        int voiceCallVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
//        int defaultVolume = mAudioManager.getStreamVolume(AudioManager.USE_DEFAULT_STREAM_TYPE);

        String volumeInfo = String.format("\nalarmVolume: %d" +
                "\n" +
                "dtmfVolume: %d" +
                "\n" +
                "musicVolume: %d" +
                "\n" +
                "notifVolume: %d" +
                "\n" +
                "voiceCallVolume: %d" +
                "\nsystemVolume: %d" +
                "\nringVolume: %d" , alarmVolume, dtmfVolume, musicVolume, notifVolume, voiceCallVolume, systemVolume,ringVolume);
        infoTv.setText(volumeInfo);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alarmModeBt:
//                mAudioManager.
        }
    }
}
