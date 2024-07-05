package com.example.bmicalculator;

public class Route {
    private String name;
    private String difficulty;
    private int elevation;
    private double distance;
    private String slope;
    private int imageResId;

    public Route(String name, String difficulty, int elevation, double distance, String slope, int imageResId) {
        this.name = name;
        this.difficulty = difficulty;
        this.elevation = elevation;
        this.distance = distance;
        this.slope = slope;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getElevation() {
        return elevation;
    }

    public double getDistance() {
        return distance;
    }

    public String getSlope() {
        return slope;
    }

    public int getImageResId() {
        return imageResId;
    }
}
