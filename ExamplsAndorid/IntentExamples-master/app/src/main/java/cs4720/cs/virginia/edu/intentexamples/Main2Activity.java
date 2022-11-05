package cs4720.cs.virginia.edu.intentexamples;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Intent Example", "Activity 2 onCreate");
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Toast.makeText(this, intent.getStringExtra(Intent.EXTRA_TEXT), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Intent Example", "Activity 2 onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Intent Example", "Activity 2 onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Intent Example", "Activity 2 onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Intent Example", "Activity 2 onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Intent Example", "Activity 2 onStop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
