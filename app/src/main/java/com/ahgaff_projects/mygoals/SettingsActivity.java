package com.ahgaff_projects.mygoals;

import static com.ahgaff_projects.mygoals.MainActivity.pref;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.LocaleListCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.ahgaff_projects.mygoals.folder.FolderRecyclerViewAdapter;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(FACTORY.getTheme(pref));
        setContentView(R.layout.settings_activity);
        setTitle(R.string.title_activity_settings);
        setupActionBar();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupActionBar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.bar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.bar_layout_toolbar);

            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        SettingsFragment.restart = () -> recreate();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));
    }

    private Context updateBaseContextLocale(Context context) {
        if (pref == null)
            return context;
        String language = pref.getString("language", "null");// Helper method to get saved language from SharedPreferences
        if (language.equals("null"))
            return context;
        Locale locale;
        switch (language) {
            case "arabic":
                locale = new Locale("ar");
                break;
            case "english":
                locale = new Locale("en");
                break;
            default:
                locale = LocaleList.getDefault().get(0);
                break;
        }
        Locale.setDefault(locale);
        return updateResourcesLocale(context, locale);
    }

    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

//            ListPreference lang = (ListPreference) findPreference("language");
//            if(lang == null)
//                Toast.makeText(getActivity(), "lang == null", Toast.LENGTH_SHORT).show();
//            else
//            lang.setOnPreferenceChangeListener((preference, newValue) -> {
//                LocaleList localeList = LocaleList.getDefault();
//                Toast.makeText(getActivity(), "0="+localeList.get(0).getCountry(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(), "-1="+localeList.get(localeList.size()-1).getCountry(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(), newValue.toString(), Toast.LENGTH_SHORT).show();
//                return true;
//            });
            if (getPreferenceScreen() != null && getPreferenceScreen().getSharedPreferences() != null)
                getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        public static MyOnMustRestart restart;

        public interface MyOnMustRestart {
            void onMustRestart();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("language")) {
                if (restart != null)
                    restart.onMustRestart();
            }else if(key.equals("Theme")){
                if (restart != null)
                    restart.onMustRestart();
            }
        }

    }

//    public void mySetLocale(Locale locale) {
//            Resources resources = getResources();
//            Configuration configuration = resources.getConfiguration();
//            configuration.setLocale(locale);
//            requireActivity().createConfigurationContext(configuration);
    ///////////////

//        Locale.setDefault(locale);
//        Configuration config = getBaseContext().getResources().getConfiguration();
//        config.locale = locale;
//        config.setLocale(locale);

    //        getBaseContext().getResources().updateConfiguration(config,
//                getBaseContext().getResources().getDisplayMetrics());
//        Toast.makeText(getBaseContext(), "setLocale called", Toast.LENGTH_SHORT).show();
//    recreate();
    /////////////////
//            LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("xx-YY");
//// Call this on the main thread as it may require Activity.restart()
//            AppCompatDelegate.setApplicationLocales(appLocale)
//    }
}