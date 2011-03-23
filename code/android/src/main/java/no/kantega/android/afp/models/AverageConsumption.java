package no.kantega.android.afp.models;

public class AverageConsumption {

    private Double day;
    private Double week;
    private Double month;

    public AverageConsumption() {
    }

    public AverageConsumption(Double day, Double week, Double month) {
        this.day = day;
        this.week = week;
        this.month = month;
    }

    public Double getDay() {
        return day;
    }

    public void setDay(Double day) {
        this.day = day;
    }

    public Double getWeek() {
        return week;
    }

    public void setWeek(Double week) {
        this.week = week;
    }

    public Double getMonth() {
        return month;
    }

    public void setMonth(Double month) {
        this.month = month;
    }
}
