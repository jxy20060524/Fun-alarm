package com.funalarm.ui;

import com.funalarm.model.Alarm;
import com.funalarm.model.User;
import com.funalarm.service.AlarmService;
import com.funalarm.service.StatsService;
import com.funalarm.service.UserService;
import com.funalarm.util.RingtoneCatalog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalTime;
import java.util.List;

public class AlarmListView {
    private final AppContext context;
    private final AlarmService alarmService = new AlarmService();
    private final StatsService statsService = new StatsService();
    private final UserService userService = new UserService();
    private VBox listBox;

    public AlarmListView(AppContext context) {
        this.context = context;
    }

    public Scene buildScene() {
        User user = context.getCurrentUser();

        Label title = new Label("我的闹钟");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 24));

        Label userLabel = new Label("你好，" + user.getNickname());
        userLabel.setFont(Font.font("Microsoft YaHei", 14));

        listBox = new VBox(10);
        listBox.setPadding(new Insets(10, 0, 10, 0));

        Button addBtn = new Button("+ 新建闹钟");
        addBtn.setOnAction(e -> openNewAlarm());

        Button statsBtn = new Button("起床统计");
        statsBtn.setOnAction(e -> context.showStats());

        Button profileBtn = new Button("修改昵称");
        profileBtn.setOnAction(e -> editNickname(user));

        Button logoutBtn = new Button("退出登录");
        logoutBtn.setOnAction(e -> {
            context.getScheduler().stop();
            context.setCurrentUser(null);
            context.showLogin();
        });

        HBox toolbar = new HBox(10, addBtn, statsBtn, profileBtn, logoutBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        ScrollPane scroll = new ScrollPane(listBox);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(320);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox root = new VBox(12, title, userLabel, toolbar, scroll);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f5f7fa;");

        refreshList();
        return new Scene(root, 520, 480);
    }

    private void refreshList() {
        listBox.getChildren().clear();
        try {
            List<Alarm> alarms = alarmService.listByUser(context.getCurrentUser().getUserId());
            if (alarms.isEmpty()) {
                listBox.getChildren().add(new Label("还没有闹钟，点击「新建闹钟」添加"));
                return;
            }
            for (Alarm alarm : alarms) {
                listBox.getChildren().add(createAlarmCard(alarm));
            }
        } catch (Exception ex) {
            listBox.getChildren().add(new Label("加载失败：" + ex.getMessage()));
        }
    }

    private VBox createAlarmCard(Alarm alarm) {
        String timeText = statsService.formatTime(alarm.getAlarmTime());
        String repeatText = statsService.formatRepeatDays(alarm.getRepeatDays());
        String label = alarm.getLabel() != null && !alarm.getLabel().isBlank() ? alarm.getLabel() : "闹钟";
        String status = alarm.isActive() ? "已启用" : "已禁用";

        Label timeLabel = new Label(timeText);
        timeLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 32));

        Label infoLabel = new Label(label + " · " + repeatText + " · " + status);

        Button editBtn = new Button("编辑");
        editBtn.setOnAction(e -> context.showAlarmEdit(alarm));

        Button deleteBtn = new Button("删除");
        deleteBtn.setOnAction(e -> deleteAlarm(alarm));

        Button toggleBtn = new Button(alarm.isActive() ? "禁用" : "启用");
        toggleBtn.setOnAction(e -> toggleAlarm(alarm));

        HBox actions = new HBox(8, editBtn, toggleBtn, deleteBtn);
        VBox card = new VBox(6, timeLabel, infoLabel, actions);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dfe6ee; -fx-border-radius: 8;");
        return card;
    }

    private void openNewAlarm() {
        Alarm alarm = new Alarm();
        alarm.setUserId(context.getCurrentUser().getUserId());
        alarm.setAlarmTime(LocalTime.of(7, 0));
        alarm.setRepeatDays("1111100");
        alarm.setRingtone(RingtoneCatalog.defaultRingtone());
        alarm.setActive(true);
        alarm.setLabel("起床");
        context.showAlarmEdit(alarm);
    }

    private void deleteAlarm(Alarm alarm) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "确定删除这个闹钟吗？", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    alarmService.delete(alarm.getAlarmId());
                    context.getScheduler().reloadAlarms();
                    refreshList();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }
        });
    }

    private void toggleAlarm(Alarm alarm) {
        try {
            alarm.setActive(!alarm.isActive());
            alarmService.update(alarm);
            context.getScheduler().reloadAlarms();
            refreshList();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void editNickname(User user) {
        TextInputDialog dialog = new TextInputDialog(user.getNickname());
        dialog.setTitle("修改昵称");
        dialog.setHeaderText(null);
        dialog.setContentText("新昵称：");
        dialog.showAndWait().ifPresent(nickname -> {
            try {
                userService.updateNickname(user.getUserId(), nickname);
                user.setNickname(nickname);
                context.showAlarmList();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
