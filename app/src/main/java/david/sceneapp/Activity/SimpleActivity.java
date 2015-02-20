package david.sceneapp.Activity;

import android.app.Activity;
import android.os.Bundle;

import david.sceneapp.R;

/**
 * Created by david on 7/2/14.
 */
public class SimpleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
    }
}
