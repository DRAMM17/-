    package controller;

    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.stage.Stage;

    /**
     * Класс точка запуска приложения
     */
    public class Main extends Application {
        private static Stage primaryStage;

        public static Stage getPrimaryStage() {
            return primaryStage;
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/view/mainForm.fxml"));
            primaryStage.setTitle("АИС Такси");
            primaryStage.setResizable(false);
            primaryStage.setScene(new Scene(root, 1010, 650));
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
