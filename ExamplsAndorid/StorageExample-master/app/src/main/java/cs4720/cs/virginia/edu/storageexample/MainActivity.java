package cs4720.cs.virginia.edu.storageexample;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends Activity {

    public static final String PREFS_NAME = "PrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String editTextValue = settings.getString("editTextValue", "none");

        EditText editText = (EditText)findViewById(R.id.editText);
        editText.setText(editTextValue);

        // Restore file
        String FILENAME = "hello_file2";
        EditText editText2 = (EditText)findViewById(R.id.editText2);

        try {
            FileInputStream fis = openFileInput(FILENAME);
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char)ch);
            }
            editText2.setText(builder.toString());
            fis.close();
        }catch(Exception e) {
            Log.e("StorageExample", e.getMessage());
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        // Using Preferences
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        EditText editText = (EditText)findViewById(R.id.editText);
        editor.putString("editTextValue", editText.getText().toString());

        // Commit the edits!
        editor.commit();

        // Using a file
        String FILENAME = "hello_file2";
        EditText editText2 = (EditText)findViewById(R.id.editText2);
        String string = editText2.getText().toString();
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }catch(Exception e) {
            Log.e("StorageExample", e.getMessage());
        }

    }

    public void saveToDB(View view) {
        // Gets the data repository in write mode
        DatabaseHelper mDbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        EditText editText = (EditText)findViewById(R.id.editText);
        EditText editText2 = (EditText)findViewById(R.id.editText2);
        String compid = editText.getText().toString();
        String name = editText2.getText().toString();
        values.put("compid", compid);
        values.put("name", name);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                "my_table",
                null,
                values);

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "compid",
                "name"
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                "compid" + " DESC";

        Cursor cursor = db.query(
                "my_table",  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        //cursor.moveToFirst();
        while(cursor.moveToNext()) {
            String currID = cursor.getString(
                    cursor.getColumnIndexOrThrow("compid")
            );
            Log.i("DBData", currID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
