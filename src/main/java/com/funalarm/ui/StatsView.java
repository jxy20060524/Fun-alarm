package com.funalarm.ui;

import com.funalarm.model.WakeSession;
import com.funalarm.service.StatsService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class StatsView {
    private final AppContext context;
    private final StatsService statsService = new StatsService();
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StatsView(AppContext context) {
        this.context = context;
    }

    public Scene buildScene() {
        Label title = new Label("起床统计");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 24));

        Label todayLabel = new Label();
        Label streakLabel = new Label();
        todayLabel.setFont(Font.font("Microsoft YaHei", 16));
        streakLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 18));

        VBox historyBox = new VBox(8);
        try {
            int userId = context.getCurrentUser().getUserId();
            Optional<java.time.LocalDateTime> todayWake = statsService.getTodayWakeTime(userId);
            int streak = statsService.getStreakDays(userId);

            todayLabel.setText("今日起床时间：" +
                    todayWake.map(t -> t.format(TIME_FMT)).orElse("尚未起床"));
            streakLabel.setText("连续早起天数：" + streak + " 天");
            streakLabel.setStyle("-fx-text-fill: #2980b9;");

            List<WakeSession> recent = statsService.getRecentSessions(userId, 10);
            if (recent.isEmpty()) {
                historyBox.getChildren().add(new Label("暂无历史记录"));
            } else {
                for (WakeSession session : recent) {
                    String status = session.isSuccess() ? "成功" : "未完成";
                    String successTime = session.getSuccessAt() != null
                            ? session.getSuccessAt().format(TIME_FMT) : "--";
                    String early = statsService.isEarlyWakeSuccess(session) ? "（早起）" : "";
                    Label row = new Label(session.getSessionDate().format(DATE_FMT)
                            + "  " + statsService.formatTime(session.getAlarmTime())
                            + "  →  " + successTime + "  " + status + early);
                    historyBox.getChildren().add(row);
                }
            }
        } catch (Exception ex) {
            todayLabel.setText("加载失败：" + ex.getMessage());
        }

        ScrollPane scroll = new ScrollPane(historyBox);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(220);

        Button backBtn = new Button("返回首页");
        backBtn.setOnAction(e -> context.showAlarmList());

        VBox root = new VBox(16, title, todayLabel, streakLabel, new Label("最近记录"), scroll, backBtn);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f5f7fa;");
        return new Scene(root, 520, 460);
    }
}
