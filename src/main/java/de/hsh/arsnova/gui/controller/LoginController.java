package de.hsh.arsnova.gui.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;

import de.hsh.arsnova.dao.ArsnovaApiDao;
import de.hsh.arsnova.gui.ScreensManager;
import de.hsh.arsnova.websocket.WebsocketClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

@Controller
public class LoginController {

	@Autowired
	ArsnovaApiDao arsnovaApi;

	@Autowired
	WebsocketClient wsClient;

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
		arsnovaApi.login(username, password, this);
		disableDuringLogin();
	}

	public void onLoginSuccess(ResponseEntity<String> response)
	{
		if(response.getStatusCode()==HttpStatus.OK)
		{
			Map<String, List<String>> header=response.getHeaders();
			outMostFor:
				for(String hName:header.keySet())
				{
					if(hName!=null&&hName.equals("Set-Cookie"))
					{
						for(String hCont:header.get(hName)){
							String[] cooks=hCont.split("; ");
							for(String s:cooks)
							{
								if(s.startsWith("JSESSIONID"))
								{
									arsnovaApi.setSessionId(s);
									break outMostFor;
								}
							}
						}
					}
				}
			Platform.runLater(()->{
				screensManager.showSessionSelectionScreen();
				wsClient.connect(arsnovaApi.getSessionId());
			});
		}
		else
		{
			Platform.runLater(()->{
				if(response.getStatusCode()==HttpStatus.UNAUTHORIZED)
					errorLabel.setText("Bad credentials");
				else
					errorLabel.setText("Error: "+response.toString());
				Timeline timeline =new Timeline(new KeyFrame(Duration.millis(3000), ae -> errorLabel.setText("")));
				timeline.play();
				enableAfterLogin();
			});
		}
	}

	public void onLoginError(Throwable t)
	{
		Platform.runLater(()->{
			String msg=t.getMessage();
			if(t instanceof HttpClientErrorException)
			{
				HttpClientErrorException hcee=(HttpClientErrorException) t;
				if(hcee.getStatusCode()==HttpStatus.UNAUTHORIZED)
					msg="Bad credentials";
			}
			errorLabel.setText("Error: "+msg);
			Timeline timeline =new Timeline(new KeyFrame(Duration.millis(3000), ae -> errorLabel.setText("")));
			timeline.play();
			enableAfterLogin();
		});
	}
	
	private void disableDuringLogin(){
		loginButton.setDisable(true);
		loginButton.setText("Connecting....");
		nameField.setDisable(true);
		passwordField.setDisable(true);
	}
	
	private void enableAfterLogin()
	{
		loginButton.setDisable(false);
		loginButton.setText("Connect");
		nameField.setDisable(false);
		passwordField.setDisable(false);
	}
}
