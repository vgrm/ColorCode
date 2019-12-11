package vgrm.colorcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPreferences ;
    private SharedPreferences.Editor mEditor;

    private Button settingsButton;
    private Button playButton;
    private Button statisticsButton;
    private Button helpButton;
    private Button aboutButton;


    private Context context = this;

    DatabaseHandler mDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mEditor = mPreferences.edit();


        String sound = mPreferences.getString(getString(R.string.pref_sound), "true");

        if(sound.equals("true")){
            Intent svc = new Intent(MainActivity.this, BackgroundSoundService.class);
            startService(svc);
        }

        checkDB();

        //CODE
        playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(playButtonClick);

        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(settingsButtonClick);

        statisticsButton = (Button) findViewById(R.id.statistics_button);
        statisticsButton.setOnClickListener(statisticsButtonClick);

        helpButton = (Button) findViewById(R.id.help_button);
        helpButton.setOnClickListener(helpButtonClick);

        aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(aboutButtonClick);

    }

    public void checkDB(){
        mDatabaseHandler = new DatabaseHandler(this);

        Cursor dataEasy = mDatabaseHandler.getDataID("easy");
        Cursor dataNormal = mDatabaseHandler.getDataID("normal");
        Cursor dataHard = mDatabaseHandler.getDataID("hard");

        //EASY DB
        int easyID = -1;
        while(dataEasy.moveToNext()){
            easyID = dataEasy.getInt(0);
        }
        if(easyID > -1){
            Log.d("DATA ID FROM DB EASY:", String.valueOf(easyID));
        } else{
            Statistics statistics = new Statistics(1, "easy", 0, 0, 0, 0, 0, 0);
            mDatabaseHandler.addData(statistics);
            Log.d("DATA ID FROM DB EASY:", "NO DATA FOUND SO ADDING DATA");
        }

        //NORMALDB
        int normalID = -1;
        while(dataNormal.moveToNext()){
            normalID = dataNormal.getInt(0);
        }
        if(normalID > -1){
            Log.d("DATA ID FROM DB NORMAL:", String.valueOf(normalID));
        } else{
            Statistics statistics = new Statistics(2, "normal", 0, 0, 0, 0, 0, 0);
            mDatabaseHandler.addData(statistics);
            Log.d("DATA ID FROM DB NORMAL:", "NO DATA FOUND SO ADDING DATA");
        }

        //HARDDB
        int hardID = -1;
        while(dataHard.moveToNext()){
            hardID = dataHard.getInt(0);
        }
        if(hardID > -1){
            Log.d("DATA ID FROM DB HARD:", String.valueOf(hardID));
        } else{
            Statistics statistics = new Statistics(3, "hard", 0, 0, 0, 0, 0, 0);
            mDatabaseHandler.addData(statistics);
            Log.d("DATA ID FROM DB HARD:", "NO DATA FOUND SO ADDING DATA");
        }
    }

    View.OnClickListener settingsButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,SettingsActivity.class);
            context.startActivity(intent);
        }
    };

    View.OnClickListener playButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,PlayActivity.class);
            context.startActivity(intent);
        }
    };

    View.OnClickListener statisticsButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,StatisticsActivity.class);
            context.startActivity(intent);
        }
    };

    View.OnClickListener helpButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,HelpActivity.class);
            context.startActivity(intent);
        }
    };

    View.OnClickListener aboutButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,AboutActivity.class);
            context.startActivity(intent);
        }
    };

}
