package com.zygon.rl.world;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author zygon
 */
public class Calendar {

    public enum Season {
        WINTER("Winter"),
        SPRING("Spring"),
        SUMMER("Summer"),
        FALL("Fall");

        private final String display;

        private Season(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }

    private static final long SEC_IN_DAY = TimeUnit.HOURS.toSeconds(24);

    private final long seconds; // since midnight
    private final int day; // TOTAL since begining of calendar
    private final int daysInYear;

    public Calendar(long seconds, int day, int daysInYear) {
        this.seconds = seconds;
        this.day = day;
        this.daysInYear = daysInYear;
    }

    public Calendar(int daysInYear) {
        this(0, 1, daysInYear);
    }

    public int getDayOfYear() {
        return day % daysInYear;
    }

    public int getYear() {
        return day / daysInYear;
    }

    public int getDaysInYear() {
        return daysInYear;
    }

    public long getHourOfDay() {
        return seconds / TimeUnit.HOURS.toSeconds(1);
    }

    public long getMinuteOfHour() {
        return TimeUnit.SECONDS.toMinutes(seconds % TimeUnit.HOURS.toSeconds(1));
    }

    public long getTotalSecondsOfDay() {
        return seconds;
    }

    /**
     * Returns the difference in seconds between the current calendar and the
     * 'old' one.
     *
     * @param old calendar
     * @return
     */
    public long getDifferenceSeconds(Calendar old) {
        if (getDaysInYear() != old.getDaysInYear()) {
            throw new IllegalArgumentException();
        }
        int yearDiff = getYear() - old.getYear();
        int dayDiff = getDayOfYear() - old.getDayOfYear();
        long secondDiff = getTotalSecondsOfDay() - old.getTotalSecondsOfDay();

        return TimeUnit.DAYS.toSeconds(yearDiff * getDaysInYear())
                + TimeUnit.DAYS.toSeconds(dayDiff)
                + secondDiff;
    }

    public Season getSeason() {
        int season = getDayOfYear() / getDaysInYear();

        switch (season) {
            case 0 -> {
                return Season.WINTER;
            }
            case 1 -> {
                return Season.SPRING;
            }
            case 2 -> {
                return Season.SUMMER;
            }
            case 3 -> {
                return Season.FALL;
            }
        }

        return null;
    }

    public Calendar addTime(long addSeconds) {
        if (addSeconds > SEC_IN_DAY) {
            // TODO: deal with this math later
            throw new IllegalArgumentException("Adding " + addSeconds + " seconds not supported");
        }

        long calcSeconds = this.seconds + addSeconds > SEC_IN_DAY
                ? (this.seconds + addSeconds) % SEC_IN_DAY
                : this.seconds + addSeconds;

        int calcDay = this.seconds + addSeconds > SEC_IN_DAY
                ? day + 1
                : day;

        return new Calendar(calcSeconds, calcDay, daysInYear);
    }

    // TODO: 24 vs 12 hour clock
    public String getTime() {
        String time = (getHourOfDay() + ":") + (getMinuteOfHour() > 9
                ? String.valueOf(getMinuteOfHour()) : ("0" + getMinuteOfHour()));

        if (getHourOfDay() < 12) {
            time += " AM";
        }

        return time;
    }
}
