package cs4720.cs.virginia.edu.listexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Contact> contacts;
    EditText nameField;
    RecyclerView rvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lookup the recyclerview in activity layout
        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        nameField = (EditText)findViewById(R.id.personName);

        // Initialize contacts
        contacts = Contact.createContactsList(1);
        // Create adapter passing in the sample user data
        ContactsAdapter adapter = new ContactsAdapter(this, contacts);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
    }

    // Called when you tap the Add Contact button
    public void addContact(View view) {
        // Make sure it is a name
        if(!nameField.getText().toString().equals("")) {
            // Log the action
            Log.d("ListExample", "addContact " + nameField.getText().toString());
            // Make a new contact
            contacts.add(new Contact(nameField.getText().toString(), true));
            // Get the adapter that manages the data set and let it know something new was added
            rvContacts.getAdapter().notifyDataSetChanged();
            // Blank the name field
            nameField.setText("");
        }
    }

    // Called tapping on an online contact
    public void sendMessage(View view) {
        TextView currentItem = (TextView)view;
        Log.d("ListExample", "sendMessage to " + currentItem.getText().toString());
        // Make Toast
        Toast.makeText(this, "Sending message to " + currentItem.getText().toString(), Toast.LENGTH_LONG).show();
    }
}
