package com.funalarm.ui;

import com.funalarm.util.AppConstants;
import com.funalarm.util.RingtonePlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.concurrent.ThreadLocalRandom;

public class MotivationView {
    private final AppContext context;
    private final int streakDays;

    public MotivationView(AppContext context, int streakDays) {
        this.context = context;
        this.streakDays = streakDays;
    }

    public Scene buildScene() {
        String message = AppConstants.MOTIVATION_MESSAGES[
                ThreadLocalRandom.current().nextInt(AppConstants.MOTIVATION_MESSAGES.length)];

        Label title = new Label(message);
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 28));
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);

        Label streakLabel = new Label("连续早起 " + streakDays + " 天");
        streakLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));
        streakLabel.setStyle("-fx-text-fill: #27ae60;");

        Button closeAlarmBtn = new Button("关闭闹钟");
        closeAlarmBtn.setPrefWidth(180);
        closeAlarmBtn.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 16));
        closeAlarmBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        Button backBtn = new Button("返回首页");
        backBtn.setPrefWidth(160);
        backBtn.setDisable(true);

        closeAlarmBtn.setOnAction(e -> {
            RingtonePlayer.stop();
            closeAlarmBtn.setDisable(true);
            closeAlarmBtn.setText("闹钟已关闭");
            backBtn.setDisable(false);
        });

        backBtn.setOnAction(e -> context.showAlarmList());

        VBox root = new VBox(24, title, streakLabel, closeAlarmBtn, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #eafaf1;");
        return new Scene(root, 520, 360);
    }
}
