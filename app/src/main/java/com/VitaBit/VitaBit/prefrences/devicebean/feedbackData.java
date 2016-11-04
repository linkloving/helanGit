package com.VitaBit.VitaBit.prefrences.devicebean;

import java.util.List;

/**
 * Created by Daniel.Xu on 2016/11/1.
 */

public class FeedbackData {

    /**
     * feedback : sao yundongjilv
     * start : 2012
     * end : 2012
     * app_version : 1
     * tracker_version : 1
     * tracker_model : 1
     * samples : [{"duration":222,"start_time":"222","state":222,"steps":222,"distance":222},{}]
     */

    private String feedback;
    private String start;
    private String end;
    private String app_version;
    private String tracker_version;
    private String tracker_model;
    /**
     * duration : 222
     * start_time : 222
     * state : 222
     * steps : 222
     * distance : 222
     */

    private List<SamplesBean> samples;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getTracker_version() {
        return tracker_version;
    }

    public void setTracker_version(String tracker_version) {
        this.tracker_version = tracker_version;
    }

    public String getTracker_model() {
        return tracker_model;
    }

    public void setTracker_model(String tracker_model) {
        this.tracker_model = tracker_model;
    }

    public List<SamplesBean> getSamples() {
        return samples;
    }

    public void setSamples(List<SamplesBean> samples) {
        this.samples = samples;
    }

    public static class SamplesBean {
        private int duration;
        private String start_time;
        private int state;
        private int steps;
        private int distance;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getSteps() {
            return steps;
        }

        public void setSteps(int steps) {
            this.steps = steps;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }
    }
}
