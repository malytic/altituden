package com.malytic.altituden.events;

public class TrackerEvent {

    public static final int STOPPED = 0;
    public static final int STARTED = 1;

    public final int status;

    public TrackerEvent(int status) {
        this.status = status;
    }
}
