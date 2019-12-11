package vgrm.colorcode;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by vgrmm on 2019-10-14.
 */

public class StatisticsActivity extends AppCompatActivity {

    DatabaseHandler mDatabaseHandler;

    private TextView easyGamesPlayed;
    private TextView easyGamesWon;
    private TextView easyMinGuess;
    private TextView easyMinTime;
    private TextView easyAvgGuess;
    private TextView easyAvgTime;

    private TextView normalGamesPlayed;
    private TextView normalGamesWon;
    private TextView normalMinGuess;
    private TextView normalMinTime;
    private TextView normalAvgGuess;
    private TextView normalAvgTime;

    private TextView hardGamesPlayed;
    private TextView hardGamesWon;
    private TextView hardMinGuess;
    private TextView hardMinTime;
    private TextView hardAvgGuess;
    private TextView hardAvgTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_statistics);

        int easyDataID = getDatabaseID("easy");
        int normalDataID = getDatabaseID("normal");
        int hardDataID = getDatabaseID("hard");

        //TEXTVIEWS
        easyGamesPlayed = (TextView) findViewById(R.id.textViewEasyGamesPlayed);
        easyGamesWon = (TextView) findViewById(R.id.textViewEasyGamesWon);
        easyMinGuess = (TextView) findViewById(R.id.textViewEasyMinGuess);
        easyMinTime = (TextView) findViewById(R.id.textViewEasyShortestTime);
        easyAvgGuess = (TextView) findViewById(R.id.textViewEasyAvgGuess);
        easyAvgTime = (TextView) findViewById(R.id.textViewEasyAvgTime);

        normalGamesPlayed = (TextView) findViewById(R.id.textViewNormalGamesPlayed);
        normalGamesWon = (TextView) findViewById(R.id.textViewNormalGamesWon);
        normalMinGuess = (TextView) findViewById(R.id.textViewNormalMinGuess);
        normalMinTime = (TextView) findViewById(R.id.textViewNormalShortestTime);
        normalAvgGuess = (TextView) findViewById(R.id.textViewNormalAvgGuess);
        normalAvgTime = (TextView) findViewById(R.id.textViewNormalAvgTime);

        hardGamesPlayed = (TextView) findViewById(R.id.textViewHardGamesPlayed);
        hardGamesWon = (TextView) findViewById(R.id.textViewHardGamesWon);
        hardMinGuess = (TextView) findViewById(R.id.textViewHardMinGuess);
        hardMinTime = (TextView) findViewById(R.id.textViewHardShortestTime);
        hardAvgGuess = (TextView) findViewById(R.id.textViewHardAvgGuess);
        hardAvgTime = (TextView) findViewById(R.id.textViewHardAvgTime);

        if(easyDataID > -1) {

            Statistics easyData = mDatabaseHandler.getData(easyDataID);

            long millis = easyData.getMinTime() % 1000;
            long second = (easyData.getMinTime() / 1000) % 60;
            long minute = (easyData.getMinTime() / (1000 * 60)) % 60;
            long hour = (easyData.getMinTime() / (1000 * 60 * 60)) % 24;
            String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);

            long millisAVG = easyData.getAvgTime() % 1000;
            long secondAVG = (easyData.getAvgTime() / 1000) % 60;
            long minuteAVG = (easyData.getAvgTime() / (1000 * 60)) % 60;
            long hourAVG = (easyData.getAvgTime() / (1000 * 60 * 60)) % 24;
            String timeAVG = String.format("%02d:%02d:%02d.%d", hourAVG, minuteAVG, secondAVG, millisAVG);


            easyGamesPlayed.setText(getBaseContext().getText(R.string.stat_gamesplayed) + " " + String.valueOf(easyData.getGamesPlayed()));
            easyGamesWon.setText(getBaseContext().getText(R.string.stat_gameswon) + " " + String.valueOf(easyData.getGamesWon()));
            easyMinGuess.setText(getBaseContext().getText(R.string.stat_minguess) + " " + String.valueOf(easyData.getMinGuess()));
            easyMinTime.setText(getBaseContext().getText(R.string.stat_shortesttime) + " " + time);
            easyAvgGuess.setText(getBaseContext().getText(R.string.stat_avgguesses) + " " + String.valueOf(easyData.getAvgGuess()));
            easyAvgTime.setText(getBaseContext().getText(R.string.stat_avgtime) + " " + timeAVG);
        }

        if(normalDataID > -1) {
            Statistics normalData = mDatabaseHandler.getData(normalDataID);

            long millis = normalData.getMinTime() % 1000;
            long second = (normalData.getMinTime() / 1000) % 60;
            long minute = (normalData.getMinTime() / (1000 * 60)) % 60;
            long hour = (normalData.getMinTime() / (1000 * 60 * 60)) % 24;
            String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);

            long millisAVG = normalData.getAvgTime() % 1000;
            long secondAVG = (normalData.getAvgTime() / 1000) % 60;
            long minuteAVG = (normalData.getAvgTime() / (1000 * 60)) % 60;
            long hourAVG = (normalData.getAvgTime() / (1000 * 60 * 60)) % 24;
            String timeAVG = String.format("%02d:%02d:%02d.%d", hourAVG, minuteAVG, secondAVG, millisAVG);

            normalGamesPlayed.setText(getBaseContext().getText(R.string.stat_gamesplayed) + " " + String.valueOf(normalData.getGamesPlayed()));
            normalGamesWon.setText(getBaseContext().getText(R.string.stat_gameswon) + " " + String.valueOf(normalData.getGamesWon()));
            normalMinGuess.setText(getBaseContext().getText(R.string.stat_minguess) + " " + String.valueOf(normalData.getMinGuess()));
            normalMinTime.setText(getBaseContext().getText(R.string.stat_shortesttime) + " " + time);
            normalAvgGuess.setText(getBaseContext().getText(R.string.stat_avgguesses) + " " + String.valueOf(normalData.getAvgGuess()));
            normalAvgTime.setText(getBaseContext().getText(R.string.stat_avgtime) + " " + timeAVG);

        }


        if(hardDataID > -1) {
            Statistics hardData = mDatabaseHandler.getData(hardDataID);

            long millis = hardData.getMinTime() % 1000;
            long second = (hardData.getMinTime() / 1000) % 60;
            long minute = (hardData.getMinTime() / (1000 * 60)) % 60;
            long hour = (hardData.getMinTime() / (1000 * 60 * 60)) % 24;
            String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);

            long millisAVG = hardData.getAvgTime() % 1000;
            long secondAVG = (hardData.getAvgTime() / 1000) % 60;
            long minuteAVG = (hardData.getAvgTime() / (1000 * 60)) % 60;
            long hourAVG = (hardData.getAvgTime() / (1000 * 60 * 60)) % 24;
            String timeAVG = String.format("%02d:%02d:%02d.%d", hourAVG, minuteAVG, secondAVG, millisAVG);

            hardGamesPlayed.setText(getBaseContext().getText(R.string.stat_gamesplayed) + " " + String.valueOf(hardData.getGamesPlayed()));
            hardGamesWon.setText(getBaseContext().getText(R.string.stat_gameswon) + " " + String.valueOf(hardData.getGamesWon()));
            hardMinGuess.setText(getBaseContext().getText(R.string.stat_minguess) + " " + String.valueOf(hardData.getMinGuess()));
            hardMinTime.setText(getBaseContext().getText(R.string.stat_shortesttime) + " " + time);
            hardAvgGuess.setText(getBaseContext().getText(R.string.stat_avgguesses) + " " + String.valueOf(hardData.getAvgGuess()));
            hardAvgTime.setText(getBaseContext().getText(R.string.stat_avgtime) + " " + timeAVG);
        }

    }

    private int getDatabaseID(String name){
        mDatabaseHandler = new DatabaseHandler(this);

        Cursor data = mDatabaseHandler.getDataID(name);
        int ID = -1;
        while(data.moveToNext()){
            ID = data.getInt(0);
        }
        return ID;
    }
}
