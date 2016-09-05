package de.hsh.arsnova;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.hsh.arsnova.gui.ScreensManager;
import de.hsh.arsnova.util.SpringFxmlLoader;
import de.hsh.arsnova.websocket.WebsocketClient;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application{

	private static final Logger logger=Logger.getLogger(MainApp.class);
	
	private ApplicationContext appContext;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		appContext=new AnnotationConfigApplicationContext(AppConfig.class);
		String name=appContext.getEnvironment().getProperty("application.name");
		String version=appContext.getEnvironment().getProperty("application.version");
		
		appContext.getBean(SpringFxmlLoader.class).setApplicationContext(appContext);
		
		primaryStage.setOnCloseRequest(we->{
			appContext.getBean(WebsocketClient.class).disconnect();
			appContext.getBean(ScreensManager.class).closeAnswers();
		});
		
		ScreensManager screensManager=appContext.getBean(ScreensManager.class);
		primaryStage.setTitle(String.format("%s %s", name, version));
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setAlwaysOnTop(true);
		screensManager.setPrimaryStage(primaryStage);
		screensManager.showLoginScreen();
		
		logger.log(Level.INFO, String.format("%s %s was launched", name, version));
	}

	public static void main(String[] args)
	{
		launch(args);
	}
	
}
