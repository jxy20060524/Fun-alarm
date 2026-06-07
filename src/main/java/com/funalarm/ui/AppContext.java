package com.funalarm.ui;

import com.funalarm.model.User;
import com.funalarm.scheduler.AlarmScheduler;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppContext {
    private final Stage primaryStage;
    private final AlarmScheduler scheduler = new AlarmScheduler();
    private User currentUser;

    public AppContext(Stage primaryStage) {
        this.primaryStage = primaryStage;
        scheduler.setRingHandler((alarm, context) -> {
            RingView ringView = new RingView(this, alarm, context.session(), context.question());
            primaryStage.setScene(ringView.buildScene());
            primaryStage.setFullScreen(true);
            primaryStage.setAlwaysOnTop(true);
            primaryStage.toFront();
        });
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public AlarmScheduler getScheduler() {
        return scheduler;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            scheduler.start(user);
        } else {
            scheduler.stop();
        }
    }

    public void showLogin() {
        primaryStage.setFullScreen(false);
        primaryStage.setAlwaysOnTop(false);
        primaryStage.setScene(new LoginView(this).buildScene());
    }

    public void showAlarmList() {
        primaryStage.setFullScreen(false);
        primaryStage.setAlwaysOnTop(false);
        primaryStage.setScene(new AlarmListView(this).buildScene());
    }

    public void showAlarmEdit(com.funalarm.model.Alarm alarm) {
        primaryStage.setScene(new AlarmEditView(this, alarm).buildScene());
    }

    public void showStats() {
        primaryStage.setScene(new StatsView(this).buildScene());
    }

    public void showMotivation(int streakDays) {
        primaryStage.setFullScreen(false);
        primaryStage.setAlwaysOnTop(false);
        primaryStage.setScene(new MotivationView(this, streakDays).buildScene());
    }

    public void applyScene(Scene scene) {
        primaryStage.setScene(scene);
    }
}
