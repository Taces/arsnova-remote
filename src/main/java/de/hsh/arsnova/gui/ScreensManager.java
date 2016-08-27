package de.hsh.arsnova.gui;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hsh.arsnova.util.SpringFxmlLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class ScreensManager implements Initializable{

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
	
	private void loadAndShowScreen(String url)
	{
		Parent root=(Parent) fxmlLoader.load(url);
		Scene scene=new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		
	}
	
}
