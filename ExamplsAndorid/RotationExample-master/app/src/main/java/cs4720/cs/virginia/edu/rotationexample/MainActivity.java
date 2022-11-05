package cs4720.cs.virginia.edu.rotationexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView number;
    private int value = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            Could do an if/else here to check to see if
            savedInstanceState has anything in it.
            Or you can override the methods below as shown.
            These methods are called as a part of onStart().

         */

        number = (TextView)findViewById(R.id.number_display);
        number.setText("" + value);
    }

    public void goUp(View view) {
        value +=1;
        number.setText("" + value);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        Log.d("RotationExample", "Rotating!");
        savedInstanceState.putInt("score", value);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        Log.d("RotationExample", "Rebuilding the View!");
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        value = savedInstanceState.getInt("score");
        number.setText("" + value);
    }
}
