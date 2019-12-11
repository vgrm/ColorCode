package vgrm.colorcode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vgrmm on 2019-12-09.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";

    private static final String DATABASE_NAME = "database";
    private static final String TABLE_STATS = "statistics";

    //statistics table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_GAMES_PLAYED = "games_played";
    private static final String KEY_GAMES_WON = "games_won";
    private static final String KEY_MIN_GUESS = "min_guess";
    private static final String KEY_MIN_TIME = "min_time";
    private static final String KEY_AVG_GUESS = "avg_guess";
    private static final String KEY_AVG_TIME = "avg_time";

    public DatabaseHandler(Context context) {
        super(context, TABLE_STATS, null, 9);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_STATS = "CREATE TABLE " + TABLE_STATS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_GAMES_PLAYED + " INTEGER,"
                + KEY_GAMES_WON + " INTEGER,"
                + KEY_MIN_GUESS + " INTEGER,"
                + KEY_MIN_TIME + " INTEGER,"
                + KEY_AVG_GUESS + " REAL,"
                + KEY_AVG_TIME + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE_STATS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addData(Statistics statistics){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, statistics.getName());
        values.put(KEY_GAMES_PLAYED, statistics.getGamesPlayed());
        values.put(KEY_GAMES_WON, statistics.getGamesWon());
        values.put(KEY_MIN_GUESS, statistics.getMinGuess());
        values.put(KEY_MIN_TIME, statistics.getMinTime());
        values.put(KEY_AVG_GUESS, statistics.getAvgGuess());
        values.put(KEY_AVG_TIME, statistics.getAvgTime());

        Log.d("vlues add data table", values.toString());

        long idValue = db.insert(TABLE_STATS, null, values);
        db.close();
        return idValue;
    }
    /*
    public void updateData(int id, int gamesPlayed, int gamesWon, int minGuess, String minTime, double avgGuess, int avgTime){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAMES_PLAYED, gamesPlayed);
        values.put(KEY_GAMES_WON, gamesWon);
        values.put(KEY_MIN_GUESS, minGuess);
        values.put(KEY_MIN_TIME, minTime);
        values.put(KEY_AVG_GUESS, avgGuess);
        values.put(KEY_AVG_TIME, avgTime);

        String whereArgs[] = {String.valueOf(id)};
        db.update(TABLE_STATS, values, KEY_ID + " = ?", whereArgs);
        db.close();
    }*/

    public void updateData(Statistics statistics){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, statistics.getName());
        values.put(KEY_GAMES_PLAYED, statistics.getGamesPlayed());
        values.put(KEY_GAMES_WON, statistics.getGamesWon());
        values.put(KEY_MIN_GUESS, statistics.getMinGuess());
        values.put(KEY_MIN_TIME, statistics.getMinTime());
        values.put(KEY_AVG_GUESS, statistics.getAvgGuess());
        values.put(KEY_AVG_TIME, statistics.getAvgTime());

        //Log.d("vlues", values.toString());
        //Log.d("DB ID", String.valueOf(statistics.getID()));
        //String whereArgs[] = {String.valueOf(id)};
        db.update(TABLE_STATS, values, KEY_ID + "=?",
                new String[] { String.valueOf(statistics.getID())});
        db.close();
    }
    /*
        public void updateData(Statistics statistics){
            SQLiteDatabase db = this.getWritableDatabase();

            String query = "UPDATE " + TABLE_STATS + " SET " +
                    KEY_NAME + " = '" + statistics.getName() + "', " +
                    KEY_GAMES_PLAYED + " = '" + statistics.getGamesPlayed() + "', " +
                    KEY_GAMES_WON + " = '" + statistics.getGamesPlayed() + "', " +
                    KEY_MIN_GUESS + " = '" + statistics.getMinGuess() + "', " +
                    KEY_MIN_TIME + " = '" + statistics.getMinTime() + "', " +
                    KEY_AVG_GUESS + " = '" + statistics.getAvgGuess() + "', " +
                    KEY_AVG_TIME + " = '" + statistics.getAvgTime() + "' WHERE " +
                    KEY_ID + " = " + statistics.getID();

            db.execSQL(query);

            Log.d("vlues update", query);
        }
    */
    public Statistics getData(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_STATS, new String[] {
                        KEY_ID,
                        KEY_NAME,
                        KEY_GAMES_PLAYED,
                        KEY_GAMES_WON,
                        KEY_MIN_GUESS,
                        KEY_MIN_TIME,
                        KEY_AVG_GUESS,
                        KEY_AVG_TIME
                }, KEY_ID + "=?",
                new String[] {String.valueOf(id)},null,null,null,null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        Statistics statistics = new Statistics(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                Integer.parseInt(cursor.getString(2)),
                Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)),
                Integer.parseInt(cursor.getString(5)),
                Double.parseDouble(cursor.getString(6)),
                Integer.parseInt(cursor.getString(7)));
        cursor.close();
        db.close();
        return statistics;

    }

    public Cursor getDataID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_ID + " FROM " + TABLE_STATS +
                " WHERE " + KEY_NAME + " = '" + name + "'";

        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public void deleteData(Statistics statistics){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STATS,KEY_ID + "=?", new String[] { String.valueOf(statistics.getID())});
        //db.close();
    }

}
