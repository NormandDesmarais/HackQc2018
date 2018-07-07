package com.example.payne.simpletestapp.Objects;

import com.example.payne.simpletestapp.DeviceStorage.Preferences;

public class MonitoredZone {

    private double topCoord = 0.0;
    private double botCoord = 0.0;
    private double leftCoord = 0.0;
    private double rightCoord = 0.0;


    public MonitoredZone(double topCoord, double botCoord, double rightCoord, double leftCoord) {
        this.topCoord = topCoord;
        this.botCoord = botCoord;
        this.leftCoord = leftCoord;
        this.rightCoord = rightCoord;
    }


    public double getTopCoord() { return topCoord; }
    public void setTopCoord(double topCoord) { this.topCoord = topCoord; }
    public double getBotCoord() { return botCoord; }
    public void setBotCoord(double botCoord) { this.botCoord = botCoord; }
    public double getLeftCoord() { return leftCoord; }
    public void setLeftCoord(double leftCoord) { this.leftCoord = leftCoord; }
    public double getRightCoord() { return rightCoord; }
    public void setRightCoord(double rightCoord) { this.rightCoord = rightCoord; }

    @Override
    public String toString() {
        return Preferences.MZS_SEPARATOR + topCoord +
                Preferences.MZ_COORD_SEPARATOR + botCoord +
                Preferences.MZ_COORD_SEPARATOR + rightCoord+
                Preferences.MZ_COORD_SEPARATOR + leftCoord ;
    }
}
