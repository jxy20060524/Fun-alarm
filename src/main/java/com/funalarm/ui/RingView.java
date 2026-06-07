package com.funalarm.ui;

import com.funalarm.model.Alarm;
import com.funalarm.model.Question;
import com.funalarm.model.WakeSession;
import com.funalarm.service.QuizService;
import com.funalarm.service.StatsService;
import com.funalarm.util.RingtonePlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RingView {
    private final AppContext context;
    private final Alarm alarm;
    private WakeSession session;
    private Question currentQuestion;
    private final QuizService quizService = new QuizService();
    private final StatsService statsService = new StatsService();

    private Label questionLabel;
    private Label messageLabel;
    private ToggleGroup optionGroup;

    public RingView(AppContext context, Alarm alarm, WakeSession session, Question question) {
        this.context = context;
        this.alarm = alarm;
        this.session = session;
        this.currentQuestion = question;
    }

    public Scene buildScene() {
        RingtonePlayer.start();

        Label title = new Label("闹钟响了！答对题目才能关闭");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #e74c3c;");

        Label timeLabel = new Label("闹钟时间：" + statsService.formatTime(alarm.getAlarmTime()));
        timeLabel.setFont(Font.font("Microsoft YaHei", 16));

        questionLabel = new Label();
        questionLabel.setWrapText(true);
        questionLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 20));
        updateQuestionDisplay();

        optionGroup = new ToggleGroup();
        VBox optionsBox = new VBox(12);
        optionsBox.getChildren().addAll(
                createOption("A", currentQuestion.getOptionA()),
                createOption("B", currentQuestion.getOptionB()),
                createOption("C", currentQuestion.getOptionC()),
                createOption("D", currentQuestion.getOptionD())
        );

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: #d35400; -fx-font-size: 14px;");

        Button submitBtn = new Button("提交答案");
        submitBtn.setPrefSize(200, 48);
        submitBtn.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 16));
        submitBtn.setOnAction(e -> submitAnswer());

        VBox root = new VBox(20, title, timeLabel, questionLabel, optionsBox, messageLabel, submitBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #fff5f5;");

        return new Scene(root, 800, 600);
    }

    private RadioButton createOption(String key, String text) {
        RadioButton btn = new RadioButton(key + ". " + text);
        btn.setToggleGroup(optionGroup);
        btn.setUserData(key.charAt(0));
        btn.setFont(Font.font("Microsoft YaHei", 16));
        btn.setWrapText(true);
        btn.setMaxWidth(600);
        return btn;
    }

    private void updateQuestionDisplay() {
        questionLabel.setText(currentQuestion.getContent());
    }

    private void submitAnswer() {
        Toggle selected = optionGroup.getSelectedToggle();
        if (selected == null) {
            messageLabel.setText("请选择一个选项");
            return;
        }
        char answer = (Character) selected.getUserData();
        try {
            QuizService.SubmitResult result = quizService.submitAnswer(
                    session.getSessionId(), currentQuestion.getQuestionId(), answer);

            session = result.session();

            if (result.correct()) {
                RingtonePlayer.stop();
                int streak = statsService.getStreakDays(context.getCurrentUser().getUserId());
                context.showMotivation(streak);
                return;
            }

            if (result.switchedQuestion()) {
                currentQuestion = result.nextQuestion();
                optionGroup = new ToggleGroup();
                messageLabel.setText(result.message());
                rebuildOptions();
                updateQuestionDisplay();
                return;
            }

            messageLabel.setText(result.message());
        } catch (Exception ex) {
            messageLabel.setText(ex.getMessage());
        }
    }

    private void rebuildOptions() {
        Scene scene = questionLabel.getScene();
        if (scene == null) {
            return;
        }
        VBox root = (VBox) scene.getRoot();
        int optionsIndex = root.getChildren().indexOf(questionLabel) + 1;
        if (optionsIndex >= 0 && optionsIndex < root.getChildren().size()) {
            VBox optionsBox = new VBox(12);
            optionsBox.getChildren().addAll(
                    createOption("A", currentQuestion.getOptionA()),
                    createOption("B", currentQuestion.getOptionB()),
                    createOption("C", currentQuestion.getOptionC()),
                    createOption("D", currentQuestion.getOptionD())
            );
            root.getChildren().set(optionsIndex, optionsBox);
        }
    }
}
