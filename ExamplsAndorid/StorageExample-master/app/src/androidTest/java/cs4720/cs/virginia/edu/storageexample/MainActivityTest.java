package cs4720.cs.virginia.edu.storageexample;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.EditText;


public class MainActivityTest extends ActivityInstrumentationTestCase2 {
    private MainActivity mainActivity;
    private EditText sharedPrefEditText;
    private EditText fileEditText;
    private Instrumentation instrumentation;

    public MainActivityTest(Class activityClass) {
        super(activityClass);
    }

    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);
        instrumentation = getInstrumentation();


        mainActivity = (MainActivity)getActivity();
        sharedPrefEditText = (EditText)mainActivity.findViewById(R.id.editText);
        fileEditText = (EditText)mainActivity.findViewById(R.id.editText2);

    }

    @MediumTest
    public void testLayout() {
        final View decorView = mainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, sharedPrefEditText);
        ViewAsserts.assertOnScreen(decorView, fileEditText);
        assertTrue(View.GONE == sharedPrefEditText.getVisibility());
        assertTrue(View.GONE == fileEditText.getVisibility());
    }

    @SmallTest
    public void testSharedPrefs() {
        sharedPrefEditText.setText("This is a test.");
        instrumentation.callActivityOnStop(mainActivity);
        instrumentation.callActivityOnCreate(mainActivity, null);
        assertTrue(sharedPrefEditText.getText().equals("This is a test."));
    }

}
