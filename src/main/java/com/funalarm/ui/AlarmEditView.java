package com.funalarm.ui;

import com.funalarm.model.Alarm;
import com.funalarm.service.AlarmService;
import com.funalarm.util.AppConstants;
import com.funalarm.util.RingtoneCatalog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalTime;

public class AlarmEditView {
    private final AppContext context;
    private final Alarm alarm;
    private final boolean isNew;
    private final AlarmService alarmService = new AlarmService();

    public AlarmEditView(AppContext context, Alarm alarm) {
        this.context = context;
        this.alarm = alarm;
        this.isNew = alarm.getAlarmId() == 0;
    }

    public Scene buildScene() {
        Label title = new Label(isNew ? "新建闹钟" : "编辑闹钟");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, alarm.getAlarmTime().getHour());
        hourSpinner.setEditable(true);
        hourSpinner.setPrefWidth(80);

        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, alarm.getAlarmTime().getMinute(), 1);
        minuteSpinner.setEditable(true);
        minuteSpinner.setPrefWidth(80);

        CheckBox[] dayChecks = new CheckBox[7];
        GridPane dayGrid = new GridPane();
        dayGrid.setHgap(10);
        dayGrid.setVgap(8);
        String repeat = alarm.getRepeatDays() != null ? alarm.getRepeatDays() : "1111100";
        for (int i = 0; i < 7; i++) {
            dayChecks[i] = new CheckBox(AppConstants.DAY_LABELS[i]);
            dayChecks[i].setSelected(repeat.charAt(i) == '1');
            dayGrid.add(dayChecks[i], i % 4, i / 4);
        }

        ComboBox<String> ringtoneBox = new ComboBox<>();
        ringtoneBox.getItems().addAll(RingtoneCatalog.listAvailable());
        String selectedRingtone = alarm.getRingtone();
        if (selectedRingtone != null && ringtoneBox.getItems().contains(selectedRingtone)) {
            ringtoneBox.setValue(selectedRingtone);
        } else {
            ringtoneBox.setValue(RingtoneCatalog.defaultRingtone());
        }

        TextField labelField = new TextField(alarm.getLabel() != null ? alarm.getLabel() : "");
        labelField.setPromptText("备注，如「起床」");

        CheckBox activeCheck = new CheckBox("启用闹钟");
        activeCheck.setSelected(alarm.isActive());

        Label message = new Label();
        message.setStyle("-fx-text-fill: #c0392b;");

        Button saveBtn = new Button("保存");
        saveBtn.setOnAction(e -> {
            try {
                LocalTime time = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
                alarmService.validateTime(time);
                alarm.setAlarmTime(time);
                alarm.setRepeatDays(buildRepeatDays(dayChecks));
                alarm.setRingtone(ringtoneBox.getValue());
                alarm.setLabel(labelField.getText().trim());
                alarm.setActive(activeCheck.isSelected());

                if (isNew) {
                    alarmService.create(alarm);
                } else {
                    alarmService.update(alarm);
                }
                context.getScheduler().reloadAlarms();
                context.showAlarmList();
            } catch (Exception ex) {
                message.setText(ex.getMessage());
            }
        });

        Button cancelBtn = new Button("取消");
        cancelBtn.setOnAction(e -> context.showAlarmList());

        HBox timeBox = new HBox(8, new Label("时"), hourSpinner, new Label("分"), minuteSpinner);
        timeBox.setAlignment(Pos.CENTER_LEFT);
        HBox actions = new HBox(12, saveBtn, cancelBtn);

        VBox root = new VBox(14, title, new Label("闹钟时间"), timeBox,
                new Label("重复"), dayGrid,
                new Label("铃声"), ringtoneBox,
                new Label("备注"), labelField,
                activeCheck, message, actions);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f5f7fa;");
        return new Scene(root, 480, 520);
    }

    private String buildRepeatDays(CheckBox[] checks) {
        StringBuilder sb = new StringBuilder();
        for (CheckBox check : checks) {
            sb.append(check.isSelected() ? '1' : '0');
        }
        return sb.toString();
    }
}
