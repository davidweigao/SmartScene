package david.sceneapp.Activity;

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
        }
        finish();
    }
}
