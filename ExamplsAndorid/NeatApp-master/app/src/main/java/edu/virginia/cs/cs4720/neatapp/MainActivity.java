package edu.virginia.cs.cs4720.neatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView myText;
    private Button myButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myText = findViewById(R.id.textView2);
        myButton = findViewById(R.id.button);
    }

    public void changeText(View view) {
        myText.setText("You clicked the button!");
    }
}
