package com.VitaBit.VitaBit.prefrences.devicebean;

/**
 * Created by Administrator on 2016/8/11.
 */
public class VitBitData {
    private String device_id;
    private int duration;
    private String metric;
    private String start_time ;
    private int value;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "VitBitData{" +
                "device_id='" + device_id + '\'' +
                ", duration=" + duration +
                ", metric='" + metric + '\'' +
                ", start_time='" + start_time + '\'' +
                ", value=" + value +
                '}';
    }
}
