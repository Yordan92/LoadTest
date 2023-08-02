package com.webint.loadtest.dto;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MinMaxAvgDTO<T extends Comparable<T>> {
    T min;
    T max;
    T avg;
    long times;
    T sum;

    Instant instantWhenMaxIsSet;



    public MinMaxAvgDTO(T defaultValue) {
        sum = defaultValue;
    }
    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public T getAvg() {
        return avg;
    }

    public void setAvg(T avg) {
        this.avg = avg;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public T getSum() {
        return sum;
    }

    public void setSum(T sum) {
        this.sum = sum;
    }

    public void addMinMax(T value) {
        if (getMin() == null || value.compareTo(getMin()) < 0) {
            setMin(value);
        }
        if (getMax() == null ||value.compareTo(getMax()) > 0) {
            setMax(value);
            instantWhenMaxIsSet = Instant.now();
        }

    }

    public String getTimeWhenMaxIsSet() {
        return DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(Instant.now());
    }



    @Override
    public String toString() {
        if (min instanceof Double) {
            DecimalFormat df = new DecimalFormat("#.##");
            return "MinMaxAvgDTO{" +
                    "min=" + df.format(min) +
                    ", max=" + df.format(max) +
                    ", avg=" + df.format(avg) +
                    '}';
        }
        return "MinMaxAvgDTO{" +
                "min=" + min +
                ", max=" + max +
                ", avg=" + avg +
                '}';
    }
}
