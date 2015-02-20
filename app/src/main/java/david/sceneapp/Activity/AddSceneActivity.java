package david.sceneapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import david.sceneapp.R;
import david.sceneapp.Model.Scene;
import david.sceneapp.SceneStorageManager;

public class AddSceneActivity extends Activity {


    SeekBar mediaVolBar;
    SeekBar sysVolBar;
    SeekBar alarmVolBar;
    SeekBar callVolBar;
    Spinner ringerModeSpinner;
    EditText sceneNameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scene);
        mediaVolBar = (SeekBar) findViewById( R.id.mediaVolBar);
        sysVolBar = (SeekBar) findViewById(R.id.sysVolBar);
        alarmVolBar = (SeekBar) findViewById(R.id.alarmVolBar);
        callVolBar = (SeekBar) findViewById(R.id.callVolBar);
        ringerModeSpinner = (Spinner) findViewById(R.id.ringerModeSpinner);
        sceneNameET = (EditText) findViewById(R.id.sceneNameET);

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        alarmVolBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        mediaVolBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sysVolBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        callVolBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));

        alarmVolBar.setProgress(am.getStreamVolume(AudioManager.STREAM_ALARM));
        mediaVolBar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        sysVolBar.setProgress(am.getStreamVolume(AudioManager.STREAM_SYSTEM));
        callVolBar.setProgress(am.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        ringerModeSpinner.setSelection(am.getRingerMode());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_scene, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_next) {
            Scene scene = new Scene();
            scene.setMusicVolume(mediaVolBar.getProgress());
            scene.setAlarmVolume(alarmVolBar.getProgress());
            scene.setSystemVolume(sysVolBar.getProgress());
            scene.setVoiceCallVolume(callVolBar.getProgress());
            scene.setRingerMode(ringerModeSpinner.getSelectedItemPosition());
            scene.setName(sceneNameET.getText().toString());
            SceneStorageManager ssm = new SceneStorageManager(this);
            ssm.saveScene(scene);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
