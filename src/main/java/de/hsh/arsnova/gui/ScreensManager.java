package de.hsh.arsnova.gui;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hsh.arsnova.util.SpringFxmlLoader;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class ScreensManager{

	private Stage answerStage=null;

	@Autowired
	private SpringFxmlLoader fxmlLoader;

	private Stage primaryStage;

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void showLoginScreen(){
		loadAndShowScreen("de/hsh/arsnova/view/Login.fxml");
	}

	public void showSessionSelectionScreen(){
		loadAndShowScreen("de/hsh/arsnova/view/SessionSelection.fxml");
	}

	public void showSessionManagerScreen(){
		loadAndShowScreen("de/hsh/arsnova/view/SessionManager.fxml");
	}

	public void showAnswers()
	{
		Platform.runLater(()->{
			if(answerStage!=null)
				answerStage.close();
			answerStage= new Stage();
			answerStage.setTitle("Answers");
			Parent root=(Parent) fxmlLoader.load("de/hsh/arsnova/view/AnswerPanel.fxml");
			answerStage.setScene(new Scene(root, 450, 450));
			answerStage.show();
		});
	}

	public void closeAnswers()
	{
		answerStage.close();
	}

	private void loadAndShowScreen(String url)
	{
		Parent root=(Parent) fxmlLoader.load(url);
		Scene scene=new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
