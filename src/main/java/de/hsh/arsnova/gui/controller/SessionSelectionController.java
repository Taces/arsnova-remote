package de.hsh.arsnova.gui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import de.hsh.arsnova.dao.ArsnovaApiDao;
import de.hsh.arsnova.gui.ScreensManager;
import de.hsh.arsnova.model.Session;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

@Controller
public class SessionSelectionController implements Initializable{

	private HashMap<String, Session> sessions;

	@Autowired
	private ArsnovaApiDao arsnovaApi;
	
	@Autowired
	private ScreensManager screensManager;

	@FXML
	private Button enterButton;

	@FXML
	private ComboBox<String> sessionCombo;

	@FXML
	protected void enterSession(ActionEvent e)
	{
		if(sessionCombo.getValue()!=null)
		{
			arsnovaApi.setSession(sessions.get(sessionCombo.getValue()).getKeyword());
			screensManager.showSessionManagerScreen();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sessions=new HashMap<String, Session>();
		ObservableList<String> items=sessionCombo.getItems();
		for(Session s:arsnovaApi.getSessions())
		{
			String newItem=s.getName()+" ("+s.getKeyword()+")";
			items.add(newItem);
			sessions.put(newItem, s);
		}
	}

}
