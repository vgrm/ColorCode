package vgrm.colorcode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by vgrmm on 2019-10-14.
 */

public class PlayActivity extends AppCompatActivity {

    DatabaseHandler mDatabaseHandler;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private ListView myList;
    private ListAdapter adapter;

    private RadioButton blueButton;
    private RadioButton lightblueButton;
    private RadioButton greenButton;
    private RadioButton yellowButton;
    private RadioButton orangeButton;
    private RadioButton redButton;
    private RadioButton pinkButton;
    private RadioButton purpleButton;

    private Button guessButton;
    private Button homeButton;
    private Button resetButton;

    private Chronometer timePlayed;
    private long timeWhenStopped = 0;

    private int indexPeg = 0;
    private int indexGuess = 0;

    private int MAX_PEGS = 0;
    private int MAX_COLORS = 0;
    private String DB_NAME = "normal";
    private int DB_ID = -1;

    private Context context = this;

    private boolean solved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_play);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String difficulty = mPreferences.getString(getString(R.string.pref_difficulty), "normal");

        if(difficulty.equals("easy")){
            MAX_PEGS = 4;
            MAX_COLORS = 4;
            DB_NAME = "easy";
        }else if(difficulty.equals("normal")){
            MAX_PEGS = 4;
            MAX_COLORS = 6;
            DB_NAME = "normal";
        }else if(difficulty.equals("hard")){
            MAX_PEGS = 6;
            MAX_COLORS = 8;
            DB_NAME = "hard";
        } else{
            MAX_PEGS = 4;
            MAX_COLORS = 6;
            DB_NAME = "normal";
        }

        mDatabaseHandler = new DatabaseHandler(this);

        DB_ID = getDatabaseID(DB_NAME);

        if (DB_ID > -1) {
            Statistics data = mDatabaseHandler.getData(DB_ID);
            int gamesPlayed = data.getGamesPlayed() + 1;
            data.setGamesPlayed(gamesPlayed);
            mDatabaseHandler.updateData(data);
        }

        //MAX_PEGS = 4;
        //MAX_COLORS = 6;

        //color buttons
        blueButton = (RadioButton) findViewById(R.id.blueButton);
        lightblueButton = (RadioButton) findViewById(R.id.lightblueButton);
        greenButton = (RadioButton) findViewById(R.id.greenButton);
        yellowButton = (RadioButton) findViewById(R.id.yellowButton);
        orangeButton = (RadioButton) findViewById(R.id.orangeButton);
        redButton = (RadioButton) findViewById(R.id.redButton);
        pinkButton = (RadioButton) findViewById(R.id.pinkButton);
        purpleButton = (RadioButton) findViewById(R.id.purpleButton);

        if(MAX_COLORS < 8){
            purpleButton.setVisibility(View.GONE);
        }
        if(MAX_COLORS < 7){
            pinkButton.setVisibility(View.GONE);
        }
        if(MAX_COLORS < 6){
            redButton.setVisibility(View.GONE);
        }
        if(MAX_COLORS < 5){
            orangeButton.setVisibility(View.GONE);
        }

        resetButton = (Button) findViewById(R.id.resetGame);

        timePlayed = (Chronometer) findViewById(R.id.timePlayed);
        timePlayed.start();

        ImageView[] codePegs1 = {
                findViewById(R.id.codeGuessPeg00),
                findViewById(R.id.codeGuessPeg01),
                findViewById(R.id.codeGuessPeg02),
                findViewById(R.id.codeGuessPeg03),
                findViewById(R.id.codeGuessPeg04),
                findViewById(R.id.codeGuessPeg05),
        };

        for (int i = MAX_PEGS; i < 6; i++) {
            codePegs1[i].setVisibility(View.GONE);
        }

        int[] codePegsNumbers = new int[MAX_PEGS];
        Arrays.fill(codePegsNumbers, 0);
        int[] secretCode = generateCode();

        guessButton = (Button) findViewById(R.id.guessButton);
        homeButton = (Button) findViewById(R.id.backHome);
        homeButton.setOnClickListener(homeButtonClick);

        myList = (ListView) findViewById(R.id.guessListView);
        List<CodeGuess> items = new ArrayList<>();
        Intent intent = getIntent();

        adapter = new ListAdapter(this, items);
        myList.setAdapter(adapter);
        colorPegs(codePegs1, adapter, codePegsNumbers, secretCode);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //pause tasks
        timeWhenStopped = timePlayed.getBase() - SystemClock.elapsedRealtime();
        timePlayed.stop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // resume tasks

        timePlayed.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timePlayed.start();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // save the tutorial page (or something else)
        //savedInstanceState.putInt("TutPage", tutorialPage);
        //savedInstanceState.putBoolean("tutUsed", tutorialUsed);
        // more additions possible

        //savedInstanceState.putAll(savedInstanceState);
        //savedInstanceState.put

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore state
        //tutorialPage = savedInstanceState.getInt("TutPage");
        //tutorialUsed = savedInstanceState.getBoolean("tutUsed");
        //init();


    }


    View.OnClickListener homeButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    };

    private int getDatabaseID(String name){
        mDatabaseHandler = new DatabaseHandler(this);

        Cursor data = mDatabaseHandler.getDataID(name);
        int ID = -1;
        while(data.moveToNext()){
            ID = data.getInt(0);
        }
        return ID;
    }

    private void updateDB(){

        mDatabaseHandler = new DatabaseHandler(this);

        int elapsedMillis = (int) (SystemClock.elapsedRealtime() - timePlayed.getBase());

        if (DB_ID > -1) {
            Statistics data = mDatabaseHandler.getData(DB_ID);
            int gamesWon = data.getGamesWon();
            int minGuess = data.getMinGuess();
            int minTime = data.getMinTime();
            double avgGuess = data.getAvgGuess();
            int avgTime = data.getAvgTime();

            data.setGamesWon(gamesWon+1);
            if(indexGuess < minGuess || minGuess == 0) data.setMinGuess(indexGuess);
            if(elapsedMillis < minTime || minTime == 0) data.setMinTime(elapsedMillis);
            data.setAvgGuess((gamesWon*avgGuess+indexGuess)/(gamesWon+1));
            data.setAvgTime((gamesWon*avgTime+elapsedMillis)/(gamesWon+1));
            mDatabaseHandler.updateData(data);
        }
    }

    private int[] generateCode() {
        Random random = new Random();

        int[] codeArray = new int[MAX_PEGS];

        for (int i = 0; i < MAX_PEGS; i++) {
            int number = random.nextInt(MAX_COLORS-1);
            if (number == 0) {
                codeArray[i] = MAX_COLORS;
            } else {
                codeArray[i] = number;
            }
            Log.d("code", String.valueOf(codeArray[i]));
        }

        return codeArray;
    }

    private int countCorrectPos(int[] secretCode, int[] guess) {
        int correct = 0;

        for (int i = 0; i < MAX_PEGS; i++) {
            //Log.d("correctPoscounter1", String.valueOf(secretCode[i]));
            //Log.d("correctPoscounter2", String.valueOf(guess[i]));
            if (secretCode[i] == guess[i]) {
                correct++;
                System.out.print(correct);
            }
        }
        return correct;
    }

    private int countCorrectColor(int[] secretCode, int[] guess) {
        int correct = 0;

        int[] temp = new int[MAX_PEGS];

        System.arraycopy(secretCode, 0, temp, 0, MAX_PEGS);
        for (int i = 0; i < MAX_PEGS; i++) {
            for (int j = 0; j < MAX_PEGS; j++) {
                if (temp[j] == guess[i]) {
                    correct++;
                    temp[j] = 0;
                    j = MAX_PEGS;
                }
            }
        }
        return correct;
    }

    private void selectionhandler(final ImageView[] codePegs1, int index){
        for(int i = 0; i < 6; i++){
            codePegs1[i].setBackgroundResource(0);
        }

        if(index +1 > MAX_PEGS) codePegs1[0].setBackground(getDrawable(R.drawable.button_border));
        else codePegs1[index].setBackground(getDrawable(R.drawable.button_border));
    }

    private void colorPegs(final ImageView[] codePegs1, final ListAdapter adapter, final int[] codePegsNumbers, final int[] secretCode) {

        //region color state lists
        final ColorStateList[] colorStateLists = {
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                getResources().getColor(R.color.color01)
                                , getResources().getColor(R.color.color01),
                        }
                ),
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                getResources().getColor(R.color.color02)
                                , getResources().getColor(R.color.color02),
                        }
                ),
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                getResources().getColor(R.color.color03)
                                , getResources().getColor(R.color.color03),
                        }
                ),
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                getResources().getColor(R.color.color04)
                                , getResources().getColor(R.color.color04),
                        }
                ),
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                getResources().getColor(R.color.color05)
                                , getResources().getColor(R.color.color05),
                        }
                ),
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                getResources().getColor(R.color.color06)
                                , getResources().getColor(R.color.color06),
                        }
                ),
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                getResources().getColor(R.color.color07)
                                , getResources().getColor(R.color.color07),
                        }
                ),
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                getResources().getColor(R.color.color08)
                                , getResources().getColor(R.color.color08),
                        }
                ),
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[]{

                                getResources().getColor(R.color.colorAccent)
                                , getResources().getColor(R.color.colorAccent),
                        }
                )
        };

        //endregion

        //region guess color button on click listeners
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indexPeg + 1 > MAX_PEGS) {
                    indexPeg = 0;
                }
                //codePegs1[indexPeg].setColorFilter(R.color.color04);
                codePegs1[indexPeg].setColorFilter(getBaseContext().getResources().getColor(R.color.color01));
                codePegsNumbers[indexPeg] = 1;
                indexPeg++;
                //codePegs[indexPeg++].setButtonTintList(colorStateLists[0]);

                selectionhandler(codePegs1,indexPeg);

                blueButton.setChecked(false);
            }
        });

        lightblueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indexPeg + 1 > MAX_PEGS) {
                    indexPeg = 0;
                }
                codePegs1[indexPeg].setColorFilter(getBaseContext().getResources().getColor(R.color.color02));
                codePegsNumbers[indexPeg] = 2;
                indexPeg++;
                //codePegs[indexPeg++].setButtonTintList(colorStateLists[1]);
                selectionhandler(codePegs1,indexPeg);
                lightblueButton.setChecked(false);
            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indexPeg + 1 > MAX_PEGS) {
                    indexPeg = 0;
                }
                codePegs1[indexPeg].setColorFilter(getBaseContext().getResources().getColor(R.color.color03));
                codePegsNumbers[indexPeg] = 3;
                indexPeg++;
                //codePegs[indexPeg++].setButtonTintList(colorStateLists[2]);
                selectionhandler(codePegs1,indexPeg);
                greenButton.setChecked(false);
            }
        });

        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indexPeg + 1 > MAX_PEGS) {
                    indexPeg = 0;
                }
                codePegs1[indexPeg].setColorFilter(getBaseContext().getResources().getColor(R.color.color04));
                codePegsNumbers[indexPeg] = 4;
                indexPeg++;
                //codePegs[indexPeg++].setButtonTintList(colorStateLists[3]);
                selectionhandler(codePegs1,indexPeg);
                yellowButton.setChecked(false);
            }
        });

        orangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indexPeg + 1 > MAX_PEGS) {
                    indexPeg = 0;
                }
                codePegs1[indexPeg].setColorFilter(getBaseContext().getResources().getColor(R.color.color05));
                codePegsNumbers[indexPeg] = 5;
                indexPeg++;
                //codePegs[indexPeg++].setButtonTintList(colorStateLists[4]);
                selectionhandler(codePegs1,indexPeg);
                orangeButton.setChecked(false);
            }
        });

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indexPeg + 1 > MAX_PEGS) {
                    indexPeg = 0;
                }
                codePegs1[indexPeg].setColorFilter(getBaseContext().getResources().getColor(R.color.color06));
                codePegsNumbers[indexPeg] = 6;
                indexPeg++;
                //codePegs[indexPeg++].setButtonTintList(colorStateLists[5]);
                selectionhandler(codePegs1,indexPeg);
                redButton.setChecked(false);
            }
        });

        pinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indexPeg + 1 > MAX_PEGS) {
                    indexPeg = 0;
                }
                codePegs1[indexPeg].setColorFilter(getBaseContext().getResources().getColor(R.color.color07));
                codePegsNumbers[indexPeg] = 7;
                indexPeg++;
                //codePegs[indexPeg++].setButtonTintList(colorStateLists[6]);
                selectionhandler(codePegs1,indexPeg);
                pinkButton.setChecked(false);
            }
        });

        purpleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indexPeg + 1 > MAX_PEGS) {
                    indexPeg = 0;
                }
                codePegs1[indexPeg].setColorFilter(getBaseContext().getResources().getColor(R.color.color08));
                codePegsNumbers[indexPeg] = 8;
                indexPeg++;
                //codePegs[indexPeg++].setButtonTintList(colorStateLists[7]);
                selectionhandler(codePegs1,indexPeg);
                purpleButton.setChecked(false);
            }
        });
        //endregion


        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean codeEntered = true;

                for(int i = 0; i < codePegsNumbers.length; i++){
                    if(codePegsNumbers[i] == 0) codeEntered = false;
                }

                if (!solved) {
                    if (codeEntered) {
                        indexGuess += 1;
                        ImageView[] codePegsTemp = new ImageView[codePegs1.length];
                        int[] codePegsNumTemp = new int[codePegsNumbers.length];

                        System.arraycopy(
                                codePegs1, 0, codePegsTemp, 0, codePegs1.length
                        );

                        System.arraycopy(codePegsNumbers, 0, codePegsNumTemp, 0, codePegsNumbers.length);

                        int correctPos = countCorrectPos(secretCode, codePegsNumTemp);
                        int correctColor = countCorrectColor(secretCode, codePegsNumTemp) - correctPos;

                        Log.d("correctPos", String.valueOf(correctPos));
                        Log.d("correctColor", String.valueOf(correctColor));

                        CodeGuess guess = new CodeGuess(1, correctPos, correctColor, codePegsNumTemp, codePegsTemp);

                        adapter.add(guess);
                        adapter.notifyDataSetChanged();


                        //reset guess ??????
                        indexPeg = 0;
                        selectionhandler(codePegs1, indexPeg);
                        do {
                            codePegs1[indexPeg].setColorFilter(getBaseContext().getResources().getColor(R.color.colorAccent));
                            codePegsNumbers[indexPeg] = 0;
                            indexPeg++;
                            //codePegs[indexPeg++].setButtonTintList(colorStateLists[8]);
                            if (indexPeg + 1 > MAX_PEGS) {
                                indexPeg = 0;
                            }
                        } while (indexPeg != 0);

                        //CODE GUESSED
                        if (correctPos == MAX_PEGS) {
                            solved = true;

                            int elapsedMillis = (int) (SystemClock.elapsedRealtime() - timePlayed.getBase());
                            timePlayed.stop();
                            Log.d("TIMER TIME: ", String.valueOf(elapsedMillis));

                            guessButton.setText(getString(R.string.solved));
                            guessButton.setTextColor(getBaseContext().getResources().getColor(R.color.color06));
                            guessButton.setBackground(getDrawable(R.drawable.border_green));

                            updateDB();
                        }
                    }
                }
            }
        });


        //FIX RESET
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,PlayActivity.class);
                context.startActivity(intent);
            }
        });

    }

}
