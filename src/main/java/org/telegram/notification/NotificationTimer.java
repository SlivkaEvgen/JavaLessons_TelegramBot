package org.telegram.notification;

import org.telegram.googleSheets.GoogleSheetsApiController;
import lombok.SneakyThrows;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationTimer {
    private final Timer timer = new Timer();
    private final int day;
    private static final int MILLISECONDS_TO_DAY = 1000 * 60 * 60 * 24;

    public NotificationTimer(int day) {
        this.day = day;
    }

    public void start() {
        timer.schedule(new TimerTask() {
            public void run() {
                updateDataBase();
                timer.cancel();
                NotificationTimer updateTimer = new NotificationTimer(1);
                updateTimer.start();
            }

            @SneakyThrows
            private void updateDataBase() {
                GoogleSheetsApiController.service();
            }
        }, day * MILLISECONDS_TO_DAY);
    }
}
