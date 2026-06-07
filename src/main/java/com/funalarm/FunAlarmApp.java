package com.funalarm;

import com.funalarm.config.DatabaseConfig;
import com.funalarm.ui.AppContext;
import com.funalarm.util.SystemTrayManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class FunAlarmApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseConfig.init();
        } catch (Exception ex) {
            showFatalError(primaryStage, "数据库连接失败，请检查 config.properties 和 MySQL 服务。\n" + ex.getMessage());
            return;
        }

        AppContext context = new AppContext(primaryStage);
        primaryStage.setTitle("趣味闹钟");
        primaryStage.setMinWidth(420);
        primaryStage.setMinHeight(360);
        context.showLogin();

        new SystemTrayManager().install(context, primaryStage);

        primaryStage.show();
    }

    @Override
    public void stop() {
        DatabaseConfig.shutdown();
    }

    private void showFatalError(Stage stage, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR, message,
                javafx.scene.control.ButtonType.OK);
        alert.setTitle("启动失败");
        alert.showAndWait();
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
