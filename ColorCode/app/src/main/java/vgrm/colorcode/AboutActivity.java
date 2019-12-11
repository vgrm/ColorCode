package vgrm.colorcode;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

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
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by vgrmm on 2019-11-18.
 */


public class AboutActivity extends AppCompatActivity implements RequestOperator.RequestOperatorListener {

    private Button requestButton;
    private Button lab3Button;

    private TextView title;
    private TextView bodyText;

    private Context context = this;
    private ModelPost[] publication = new ModelPost[10];
    private IndicatingView indicator;
    private IndicatingView indicatorNew;

    private int progressValue = 100;
    private ProgressBar progressBar;

    private TextView countView;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);


        requestButton = (Button) findViewById(R.id.sendreq_button);
        requestButton.setOnClickListener(requestButtonClick);

        lab3Button = (Button) findViewById(R.id.lab3_button);
        lab3Button.setOnClickListener(lab3ButtonClick);

        title=(TextView) findViewById(R.id.title);
        bodyText = (TextView) findViewById(R.id.body_text);

        indicator = (IndicatingView) findViewById(R.id.generated_graphic);
        indicatorNew = (IndicatingView) findViewById(R.id.generated_graphic_new);

        requestButton.setBackgroundResource(R.drawable.request_button_style);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        countView = (TextView) findViewById(R.id.items_count);

        //setIndicatorStatus(IndicatingView.SUCCESS);

        indicatorNew.setState(IndicatingView.REQ);

        setIndicatorStatusNew(IndicatingView.REQ);

    }

    View.OnClickListener requestButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendRequest();
        }
    };

    View.OnClickListener lab3ButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,Lab3Activity.class);
            context.startActivity(intent);
        }
    };

    private void sendRequest(){
        RequestOperator ro = new RequestOperator();
        ro.setListener(this);
        ro.start();
        setIndicatorStatus(IndicatingView.LOADING);

        progressBar.setVisibility(View.VISIBLE);

        ObjectAnimator.ofInt(progressBar, "progress", progressValue)
                .setDuration(300)
                .start();

    }

    public void updatePublication(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(publication!=null){
                    title.setText(publication[1].getTitle());
                    bodyText.setText(publication[1].getBodyText());
                    System.out.println(String.valueOf(publication.length));
                    countView.setText(String.valueOf(count));
                } else{
                    title.setText("***");
                    bodyText.setText("***");
                }
            }
        });
    }
    @Override
    public void success(ModelPost publication, int count){
        this.publication[1] = publication;
        this.count = count;
        updatePublication();
        setIndicatorStatus(IndicatingView.SUCCESS);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void failed(int responseCode){
        this.publication = null;
        updatePublication();
        setIndicatorStatus(IndicatingView.FAILED);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void setIndicatorStatus(final int status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicator.setState(status);
                indicator.invalidate();
            }
        });
    }

    public void setIndicatorStatusNew(final int status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicatorNew.setState(status);
                indicatorNew.invalidate();
            }
        });
    }

}
