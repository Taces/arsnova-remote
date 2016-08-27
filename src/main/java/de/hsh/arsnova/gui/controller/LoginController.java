package de.hsh.arsnova.gui.controller;

import java.net.HttpURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import de.hsh.arsnova.dao.ArsnovaApiDao;
import de.hsh.arsnova.gui.ScreensManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import javafx.util.Pair;

@Controller
public class LoginController {
	
	@Autowired
	ArsnovaApiDao arsnovaApi;
	
	@Autowired
	ScreensManager screensManager;
	
	@FXML
	private TextField nameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button loginButton;
	
	@FXML
	private Label errorLabel;

	@FXML
	protected void login(ActionEvent e)
	{
		String username=nameField.getText();
		String password=passwordField.getText();
		Pair<Integer, String> response=arsnovaApi.login(username, password);
		if(response.getKey()!=HttpURLConnection.HTTP_OK)
		{
			if(response.getKey()==HttpURLConnection.HTTP_UNAUTHORIZED)
				errorLabel.setText("Bad credentials");
			else
				errorLabel.setText("Error: "+response.getKey()+" - "+response.getValue());
			Timeline timeline =new Timeline(new KeyFrame(Duration.millis(3000), ae -> errorLabel.setText("")));
			timeline.play();
		}
		else
			screensManager.showSessionSelectionScreen();
	}
}
