package david.sceneapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

import david.sceneapp.Model.ExceptionScene;
import david.sceneapp.R;
import david.sceneapp.SceneStorageManager;

public class AddExceptionActivity extends Activity {

    public static final String EXTRA_FROM_IM = "com.david.fromImApp";
    public static final String EXTRA_PACKAGE = "com.david.package";

    SeekBar soundSeekBar;
    CheckBox vibrateCheckBox;
    EditText senderET;
    Button okButton;
    boolean fromImApp;
    String pkgName;

    AudioManager am;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setContentView(R.layout.activity_add_exception);
        fromImApp = getIntent().getBooleanExtra(EXTRA_FROM_IM, false);
        pkgName = getIntent().getStringExtra(EXTRA_PACKAGE);

        if(!fromImApp) {
            findViewById(R.id.imAppOption).setVisibility(View.INVISIBLE);
        }

        soundSeekBar = (SeekBar) findViewById(R.id.notifVolBar);
        vibrateCheckBox = (CheckBox) findViewById(R.id.vibrateCheckBox);
        senderET = (EditText) findViewById(R.id.senderET);
        okButton = (Button) findViewById(R.id.okButton);

        soundSeekBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
//                if(fromUser) {
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Ringtone r = RingtoneManager.getRingtone(AddExceptionActivity.this, notification);
//                    r.setStreamType(AudioManager.STREAM_NOTIFICATION);
//
//                    r.play();
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        vibrateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    Vibrator v = (Vibrator) AddExceptionActivity.this
                            .getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(300);
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExceptionScene es = new ExceptionScene(soundSeekBar.getProgress(),
                        vibrateCheckBox.isChecked(), pkgName, senderET.getText().toString());
                SceneStorageManager ssm = new SceneStorageManager(AddExceptionActivity.this);
                ssm.saveException(es);
                startActivity(new Intent(AddExceptionActivity.this, MainActivity.class));
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_exception, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
