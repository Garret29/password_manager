package pl.piotrowski;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import pl.piotrowski.controller.Controller;

@SpringBootApplication
@Configuration
public class PasswordManagerApp extends Application {

    private Parent root;
    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(PasswordManagerApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Password Manager");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(PasswordManagerApp.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/loginView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        controller.changeControllerFactory(springContext::getBean);
        controller.initLoginView();
    }

    @Override
    public void stop() throws Exception {
        springContext.stop();
        springContext.close();
    }
}
