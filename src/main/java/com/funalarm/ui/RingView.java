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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class RingView {
    private static final double OPTION_BOX_SIZE = 200;

    private final AppContext context;
    private final Alarm alarm;
    private WakeSession session;
    private Question currentQuestion;
    private final QuizService quizService = new QuizService();
    private final StatsService statsService = new StatsService();

    private Label questionLabel;
    private Label messageLabel;
    private ToggleGroup optionGroup;
    private GridPane optionsGrid;

    public RingView(AppContext context, Alarm alarm, WakeSession session, Question question) {
        this.context = context;
        this.alarm = alarm;
        this.session = session;
        this.currentQuestion = question;
    }

    public Scene buildScene() {
        RingtonePlayer.start(alarm.getRingtone());

        Label title = new Label("闹钟响了！答对题目才能关闭");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #e74c3c;");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        Label timeLabel = new Label("闹钟时间：" + statsService.formatTime(alarm.getAlarmTime()));
        timeLabel.setFont(Font.font("Microsoft YaHei", 16));
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setMaxWidth(Double.MAX_VALUE);

        questionLabel = new Label();
        questionLabel.setWrapText(true);
        questionLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 20));
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setTextAlignment(TextAlignment.CENTER);
        questionLabel.setMaxWidth(720);
        updateQuestionDisplay();

        optionsGrid = buildOptionsGrid();

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setMaxWidth(720);
        messageLabel.setStyle("-fx-text-fill: #d35400; -fx-font-size: 14px;");

        Button submitBtn = new Button("提交答案");
        submitBtn.setPrefSize(200, 48);
        submitBtn.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 16));
        submitBtn.setOnAction(e -> submitAnswer());

        VBox root = new VBox(24, title, timeLabel, questionLabel, optionsGrid, messageLabel, submitBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #fff5f5;");

        return new Scene(root, 900, 700);
    }

    private GridPane buildOptionsGrid() {
        optionGroup = new ToggleGroup();
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        grid.add(createOption("A", currentQuestion.getOptionA()), 0, 0);
        grid.add(createOption("B", currentQuestion.getOptionB()), 1, 0);
        grid.add(createOption("C", currentQuestion.getOptionC()), 0, 1);
        grid.add(createOption("D", currentQuestion.getOptionD()), 1, 1);
        return grid;
    }

    private ToggleButton createOption(String key, String text) {
        ToggleButton btn = new ToggleButton(key + ". " + text);
        btn.setToggleGroup(optionGroup);
        btn.setUserData(key.charAt(0));
        btn.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 16));
        btn.setWrapText(true);
        btn.setTextAlignment(TextAlignment.CENTER);
        btn.setAlignment(Pos.CENTER);
        btn.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
        btn.setPrefSize(OPTION_BOX_SIZE, OPTION_BOX_SIZE);
        btn.setMinSize(OPTION_BOX_SIZE, OPTION_BOX_SIZE);
        btn.setMaxSize(OPTION_BOX_SIZE, OPTION_BOX_SIZE);
        btn.setStyle("""
                -fx-background-color: white;
                -fx-text-fill: #2c3e50;
                -fx-border-color: #bdc3c7;
                -fx-border-width: 2;
                -fx-border-radius: 12;
                -fx-background-radius: 12;
                -fx-padding: 16;
                """);
        btn.selectedProperty().addListener((obs, oldVal, selected) -> {
            if (selected) {
                btn.setStyle("""
                        -fx-background-color: #3498db;
                        -fx-text-fill: white;
                        -fx-border-color: #2980b9;
                        -fx-border-width: 2;
                        -fx-border-radius: 12;
                        -fx-background-radius: 12;
                        -fx-padding: 16;
                        """);
            } else {
                btn.setStyle("""
                        -fx-background-color: white;
                        -fx-text-fill: #2c3e50;
                        -fx-border-color: #bdc3c7;
                        -fx-border-width: 2;
                        -fx-border-radius: 12;
                        -fx-background-radius: 12;
                        -fx-padding: 16;
                        """);
            }
        });
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
                int streak = statsService.getStreakDays(context.getCurrentUser().getUserId());
                context.showMotivation(streak);
                return;
            }

            if (result.switchedQuestion()) {
                currentQuestion = result.nextQuestion();
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
        int optionsIndex = root.getChildren().indexOf(optionsGrid);
        if (optionsIndex >= 0) {
            optionsGrid = buildOptionsGrid();
            root.getChildren().set(optionsIndex, optionsGrid);
        }
    }
}
