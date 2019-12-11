package vgrm.colorcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by vgrmm on 2019-10-14.
 */

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences mPreferences ;
    private SharedPreferences.Editor mEditor;

    private Spinner setDifficullty;
    private Switch setSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_settings);


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();


        setDifficullty = (Spinner) findViewById(R.id.selectDifficulty);
        setSound = (Switch) findViewById(R.id.settingSoundSwitch);

        final String[] difficulties = new String[]{
                "easy",
                "normal",
                "hard"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, difficulties);
        setDifficullty.setAdapter(adapter);

        checkSharedPreferences(difficulties);

        setDifficullty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mEditor.putString(getString(R.string.pref_difficulty),difficulties[i]);
                mEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("test sound check", ""+isChecked);
                setSound.setChecked(isChecked);

                mEditor.putString(getString(R.string.pref_sound),String.valueOf(isChecked));
                mEditor.commit();

                if(isChecked){
                    Intent svc = new Intent(SettingsActivity.this, BackgroundSoundService.class);
                    startService(svc);

                } else{
                    Intent svc = new Intent(SettingsActivity.this, BackgroundSoundService.class);
                    stopService(svc);
                }
            }
        });
    }

    private void checkSharedPreferences(String[] difficulties){
        String difficulty = mPreferences.getString(getString(R.string.pref_difficulty), "normal");
        String sound = mPreferences.getString(getString(R.string.pref_sound), "true");
        //String darkTheme = mPreferences.getString(getString(R.string.pref_darktheme), "true");

        Log.d("TEST set settings", difficulty + sound);

        //get difficulty
        if(difficulty.equals(difficulties[0])){
            setDifficullty.setSelection(0);
        } else if(difficulty.equals(difficulties[1])){
            setDifficullty.setSelection(1);
        } else setDifficullty.setSelection(2);


        //get sound
        if(sound.equals("true")){
            setSound.setChecked(true);
        } else{
            setSound.setChecked(false);
        }

    }
}
