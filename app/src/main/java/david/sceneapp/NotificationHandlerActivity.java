package david.sceneapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by david on 7/3/14.
 */
public class NotificationHandlerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch(getIntent().getIntExtra("button",-1)) {
            case 1: LALALAService.currentInstance.toggleButton();
        }
        finish();
    }
}
