package vgrm.colorcode;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by vgrmm on 2019-12-09.
 */

public class BackgroundSoundService extends Service {

    private static final String TAG = null;
    MediaPlayer player;
    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.backgroundsounds);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
        Log.d("SOUND TEST", "sound started");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TO DO
        player.start();
    }
    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {
        player.stop();
        //.release();
    }
    public void onPause() {
        player.pause();
    }
    public void onResume(){
        player.start();
    }
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }

}
