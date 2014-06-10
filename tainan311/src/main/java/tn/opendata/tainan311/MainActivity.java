package tn.opendata.tainan311;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onImageClick(View view) {
        Class<?> activity = null;
        switch (view.getId()) {
            case R.id.imageButton:
                activity = NewRequestActivity.class;
                break;
            case R.id.imageButton2:
                activity = RequestListActivity.class;
                break;
            case R.id.imageButton3:
                activity = MyRequestActivity.class;
                break;
            case R.id.imageButton4:
                // FIXME: donate activity
                activity = DetailActivity.class;
                break;
        }
        if (activity != null) {
            startActivity(new Intent(this, activity));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
