package vgrm.colorcode;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by vgrmm on 2019-10-14.
 */

public class ListAdapter extends ArrayAdapter<CodeGuess> {
    private int MAX_PEGS = 0;
    private int MAX_COLORS = 0;
    private LinearLayout guessView;

    public ListAdapter(Context context, List<CodeGuess> objects) {
        super(context, R.layout.codeguess_design, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.codeguess_design, null);


            guessView = (LinearLayout) v.findViewById(R.id.linearImages);
        }
        guessView = (LinearLayout) v.findViewById(R.id.linearImages);

        //View of points
        ImageView[] correctPoints = {
                (ImageView) v.findViewById(R.id.correctPoint01),
                (ImageView) v.findViewById(R.id.correctPoint02),
                (ImageView) v.findViewById(R.id.correctPoint03),
                (ImageView) v.findViewById(R.id.correctPoint04),
                (ImageView) v.findViewById(R.id.correctPoint05),
                (ImageView) v.findViewById(R.id.correctPoint06),
                (ImageView) v.findViewById(R.id.incorrect)
        };

        CodeGuess item = getItem(position);
        int[] array = item.getCode();
        int correctPos = item.getCorrectPos();
        int correctColor = item.getCorrectColor();

        //set all points invisible
        for(int i = 0; i<correctPoints.length;i++){
            correctPoints[i].setVisibility(View.INVISIBLE);
        }

        //if incorrect guess set "INCORRECT" visible
        if(correctPos == 0 && correctColor == 0){
            correctPoints[6].setVisibility(View.VISIBLE);
        }

        //set correct positions
        for(int i = 0; i<correctPos;i++){
            correctPoints[i].setVisibility(View.VISIBLE);
            correctPoints[i].setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
        }

        //set correct colors
        for(int i = correctPos; i<correctColor+correctPos;i++){
            correctPoints[i].setVisibility(View.VISIBLE);
            correctPoints[i].setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        }


        MAX_PEGS = array.length;
        String codeString = "";

        for (int i = 0; i < array.length; i++) {
            codeString += String.valueOf(array[i]);
        }

        if(MAX_PEGS == correctPos){
            guessView.setBackground(ActivityCompat.getDrawable(getContext(),R.drawable.border_green));
        }

        ImageView[] guessPegs = {
                (ImageView) v.findViewById(R.id.codePeg01),
                (ImageView) v.findViewById(R.id.codePeg02),
                (ImageView) v.findViewById(R.id.codePeg03),
                (ImageView) v.findViewById(R.id.codePeg04),
                (ImageView) v.findViewById(R.id.codePeg05),
                (ImageView) v.findViewById(R.id.codePeg06)
        };

        for(int i = array.length; i<6;i++){
            guessPegs[i].setVisibility(View.GONE);
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) {
                guessPegs[i].setColorFilter(ActivityCompat.getColor(getContext(), R.color.color01));
            }
            if (array[i] == 2) {
                guessPegs[i].setColorFilter(ActivityCompat.getColor(getContext(), R.color.color02));
            }
            if (array[i] == 3) {
                guessPegs[i].setColorFilter(ActivityCompat.getColor(getContext(), R.color.color03));
            }
            if (array[i] == 4) {
                guessPegs[i].setColorFilter(ActivityCompat.getColor(getContext(), R.color.color04));
            }
            if (array[i] == 5) {
                guessPegs[i].setColorFilter(ActivityCompat.getColor(getContext(), R.color.color05));
            }
            if (array[i] == 6) {
                guessPegs[i].setColorFilter(ActivityCompat.getColor(getContext(), R.color.color06));
            }
            if (array[i] == 7) {
                guessPegs[i].setColorFilter(ActivityCompat.getColor(getContext(), R.color.color07));
            }
            if (array[i] == 8) {
                guessPegs[i].setColorFilter(ActivityCompat.getColor(getContext(), R.color.color08));
            }
        }
        Log.d("codeString", codeString);
        return v;
    }
}
