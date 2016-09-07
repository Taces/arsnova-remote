package de.hsh.arsnova.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import de.hsh.arsnova.gui.ScreensManager;
import de.hsh.arsnova.util.ExternalConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

@Controller
public class OptionsController implements Initializable{

	@FXML
	Button saveButton;
	
	@FXML
	TextField apiField;
	
	@Autowired
	ScreensManager screensManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		apiField.setText(ExternalConfig.getProperty(ExternalConfig.API_URL));
	}
	
	@FXML
	protected void saveOptions(ActionEvent e)
	{
		ExternalConfig.setPropertie(ExternalConfig.API_URL, apiField.getText());
		ExternalConfig.saveConfig();
		ExternalConfig.injectProperties(); 
		screensManager.closeOptionsWindow();
	}
}
