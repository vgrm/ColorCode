package vgrm.colorcode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by vgrmm on 2019-11-18.
 */

public class IndicatingView extends View {

    public static final int NOTEXECUTED = 0;
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    public static final int LOADING = 3;
    public static final int REQ = 4;

    int state = NOTEXECUTED;

    public IndicatingView(Context context) {
        super(context);
    }

    public IndicatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndicatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public int getState() {return state;}

    public void setState(int state){this.state=state;}

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint;

        switch (state){
            case SUCCESS:
                paint = new Paint();
                paint.setColor(getResources().getColor(R.color.color06));
                paint.setStrokeWidth(20f);
                //  check
                canvas.drawLine(0,0,width/2,height,paint);
                canvas.drawLine(width/2,height,width,height/2,paint);
                break;
            case FAILED:
                paint = new Paint();
                paint.setColor(getResources().getColor(R.color.color02));
                paint.setStrokeWidth(20f);
                //  X
                canvas.drawLine(0,0,width,height,paint);
                canvas.drawLine(0,height,width,0,paint);
                break;
            case LOADING:
                paint = new Paint();
                paint.setColor(getResources().getColor(R.color.color07));
                paint.setStrokeWidth(20f);
                //  triangle
                canvas.drawLine(0,height,width/2,0,paint);
                canvas.drawLine(0,height,width,height,paint);
                canvas.drawLine(width,height,width/2,0,paint);
                break;
            case REQ:
                paint = new Paint();
                Rect rect = new Rect();
                paint.setColor(getResources().getColor(R.color.color05));
                paint.setStrokeWidth(20f);
                canvas.drawRect(0,0,width/2,height/2,paint);
                break;

            default:
                break;

        }

    }


}
