package pl.piotrowski;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import pl.piotrowski.controller.Controller;

@SpringBootApplication
@Configuration
public class PasswordManagerApp extends Application {

    private Parent root;
    private ConfigurableApplicationContext springContext;
    private Controller controller;

    public static void main(String[] args)
    {
        launch(PasswordManagerApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Password Manager");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        controller.initLoginView();
    }

    @Override
    public void init() throws Exception {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(PasswordManagerApp.class);
        springApplicationBuilder.headless(false);
        springContext = springApplicationBuilder.run();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/loginView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.changeControllerFactory(springContext::getBean);
    }

    @Override
    public void stop() {
        springContext.stop();
        springContext.close();
    }
}
