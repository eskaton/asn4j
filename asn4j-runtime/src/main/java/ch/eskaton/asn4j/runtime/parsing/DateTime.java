package ch.eskaton.asn4j.runtime.parsing;

public class DateTime {

    private int year;

    private int month;

    private int day;

    private int hour;

    private int minute;

    private int second;

    private Integer offset;

    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public DateTime(int year, int month, int day, int hour, int minute, int second, int offset) {
        this(year, month, day, hour, minute, second);

        this.offset = offset;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

}
