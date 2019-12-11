package vgrm.colorcode;

/**
 * Created by vgrmm on 2019-12-09.
 */

public class Statistics {

    private int id;
    private String name;

    private int gamesPlayed;
    private int gamesWon;
    private int minGuess;
    private int minTime;
    private double avgGuess;
    private int avgTime;

    public Statistics(){}

    public Statistics(int id, String name, int gamesPlayed, int gamesWon, int minGuess, int minTime, double avgGuess, int avgTime){
        this.id = id;
        this.name = name;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.minGuess = minGuess;
        this.minTime = minTime;
        this.avgGuess = avgGuess;
        this.avgTime = avgTime;
    }

    public int getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getGamesPlayed(){
        return gamesPlayed;
    }

    public int getGamesWon(){
        return gamesWon;
    }

    public int getMinGuess(){
        return minGuess;
    }

    public int getMinTime(){
        return minTime;
    }

    public double getAvgGuess(){
        return avgGuess;
    }

    public int getAvgTime(){
        return avgTime;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setGamesPlayed(int gamesPlayed){
        this.gamesPlayed = gamesPlayed;
    }

    public void setGamesWon(int gamesWon){
        this.gamesWon = gamesWon;
    }

    public void setMinGuess(int minGuess){
        this.minGuess = minGuess;
    }

    public void setMinTime(int minTime){
        this.minTime=minTime;
    }

    public void setAvgGuess(double avgGuess){
        this.avgGuess = avgGuess;
    }

    public void setAvgTime(int avgTime){
        this.avgTime = avgTime;
    }

}
