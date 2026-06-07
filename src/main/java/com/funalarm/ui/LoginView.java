package com.funalarm.ui;

import com.funalarm.model.User;
import com.funalarm.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {
    private final AppContext context;
    private final UserService userService = new UserService();

    public LoginView(AppContext context) {
        this.context = context;
    }

    public Scene buildScene() {
        Label title = new Label("趣味闹钟");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 28));

        TextField usernameField = new TextField();
        usernameField.setPromptText("用户名");
        usernameField.setMaxWidth(280);

        TextField nicknameField = new TextField();
        nicknameField.setPromptText("昵称（注册时使用）");
        nicknameField.setMaxWidth(280);

        Label message = new Label();
        message.setStyle("-fx-text-fill: #c0392b;");

        Button loginBtn = new Button("登录");
        loginBtn.setPrefWidth(120);
        loginBtn.setOnAction(e -> {
            try {
                User user = userService.login(usernameField.getText());
                context.setCurrentUser(user);
                context.showAlarmList();
            } catch (Exception ex) {
                message.setText(ex.getMessage());
            }
        });

        Button registerBtn = new Button("注册");
        registerBtn.setPrefWidth(120);
        registerBtn.setOnAction(e -> {
            try {
                User user = userService.register(usernameField.getText(), nicknameField.getText());
                message.setText("注册成功，请登录");
                usernameField.setText(user.getUsername());
            } catch (Exception ex) {
                message.setText(ex.getMessage());
            }
        });

        VBox box = new VBox(14, title,
                new Label("用户名"), usernameField,
                new Label("昵称"), nicknameField,
                message,
                loginBtn, registerBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setStyle("-fx-background-color: #f5f7fa;");

        Scene scene = new Scene(box, 420, 380);
        return scene;
    }
}
