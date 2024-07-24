//選擇路線清單的RecyclerView相關
package com.example.bmicalculator;

public class Route {
    private String name;
    private String difficulty;
    private String elevation;
    private String distance;
    private String slope;
    private int imageResId;
    private String startPoint;
    private String endPoint;

    public Route(String name, String difficulty, String elevation, String distance, String slope, int imageResId, String startPoint, String endPoint) {
        this.name = name;
        this.difficulty = difficulty;
        this.elevation = elevation;
        this.distance = distance;
        this.slope = slope;
        this.imageResId = imageResId;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public String getName() {
        return name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getElevation() {
        return elevation;
    }

    public String getDistance() { return distance; }

    public String getSlope() {
        return slope;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getStartPoint() { return startPoint; }

    public String getEndPoint() { return endPoint; }
}
