package at.ac.tuwien.sepm.musicplayer;

import at.ac.tuwien.sepm.musicplayer.presentation.PlayerMainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Lena Lenz.
 */
public class Main extends Application{

    private static Logger logger = Logger.getLogger(Main.class);


    public static void main(String... args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // configure log4j
        BasicConfigurator.configure();
        logger.debug("log4j configured");

        // configure spring
        final ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        logger.debug("spring configured");

        FXMLLoader fxmlLoader = new FXMLLoader();
        //add spring context to JavaFX
        fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                return ctx.getBean(clazz);
            }
        });
        Parent root = fxmlLoader.load(getClass().getResourceAsStream("/PlayerMainGui.fxml"));

        PlayerMainController mainController = fxmlLoader.getController();
        mainController.setApplicationContext(ctx);

        logger.info("starting application");
        primaryStage.setTitle("bTunes");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(700);
        primaryStage.setOnCloseRequest(event -> {
            logger.info("closing application");
            //controller.saveOnClose();
        });

        primaryStage.show();
    }
}
