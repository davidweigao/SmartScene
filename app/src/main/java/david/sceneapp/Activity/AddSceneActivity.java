package david.sceneapp.Activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import butterknife.ButterKnife;
import butterknife.InjectView;
import david.sceneapp.R;
import david.sceneapp.Model.Scene;
import david.sceneapp.SceneStorageManager;

public class AddSceneActivity extends Activity {

    private static final String TAG = AddSceneActivity.class.getSimpleName();

    @InjectView(R.id.configView)        View     configView;
    @InjectView(R.id.setNameView)       View     setNameView;
    @InjectView(R.id.mediaVolBar)       SeekBar  mediaVolBar;
    @InjectView(R.id.sysVolBar)         SeekBar  sysVolBar;
    @InjectView(R.id.alarmVolBar)       SeekBar  alarmVolBar;
    @InjectView(R.id.callVolBar)        SeekBar  callVolBar;
    @InjectView(R.id.ringerModeSpinner) Spinner  ringerModeSpinner;
    @InjectView(R.id.sceneNameET)       EditText sceneNameET;

    private Animation mFadeOutAnimation;
    private Animation mFadeInAnimation;

    private static final int STEP_CONFIG = 0;
    private static final int STEP_NAMEIT = 1;
    private int step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scene);
        ButterKnife.inject(this);
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

        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        mFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (step == STEP_NAMEIT) {
                    Log.d(TAG, "onAnimate");
                    configView.setVisibility(View.GONE);
                } else {
                    setNameView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        mFadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(step == STEP_NAMEIT) {
                    setNameView.setVisibility(View.VISIBLE);
                } else {
                    configView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        setNameView.setVisibility(View.INVISIBLE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_scene, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            switch (step) {
                case STEP_CONFIG:
                    step = STEP_NAMEIT;
                    configView.startAnimation(mFadeOutAnimation);
                    setNameView.startAnimation(mFadeInAnimation);
                    break;
                case STEP_NAMEIT:
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
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (step) {
            case STEP_CONFIG:
                super.onBackPressed();
                break;
            case STEP_NAMEIT:
                step = STEP_CONFIG;
                configView.startAnimation(mFadeInAnimation);
                setNameView.startAnimation(mFadeOutAnimation);
                break;
        }
    }

}
