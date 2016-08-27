package de.hsh.arsnova.gui.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import de.hsh.arsnova.dao.ArsnovaApiDao;
import de.hsh.arsnova.gui.ScreensManager;
import de.hsh.arsnova.model.LecturerQuestion;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

@Controller
public class SessionManagerController implements Initializable{

	private HashMap<String, LecturerQuestion> questions;

	@Autowired
	private ScreensManager screensManager;

	@Autowired
	private ArsnovaApiDao arsnovaApi;

	@FXML
	Button backButton;

	@FXML
	ComboBox<String> questionCombo;
	
	@FXML
	ComboBox<String> subjectCombo;

	@FXML
	TextField countdownTextfield;

	@FXML
	Button openQuestionButton;

	@FXML
	protected void goBack(ActionEvent e)
	{
		screensManager.showSessionSelectionScreen();
	}

	@FXML
	protected void openQuestion(ActionEvent e)
	{
		if(questionCombo.getValue()==null)
			return;
		LecturerQuestion lq=questions.get(questionCombo.getValue());
	}

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		questions=new HashMap<String, LecturerQuestion>();
		LecturerQuestion[] lqs=arsnovaApi.getLecturerQuestion();
		//Sort questions by creation subject
		Arrays.sort(lqs, (lq1, lq2)-> lq1.getSubject().compareTo(lq2.getSubject()));
		HashSet<String> subs=new HashSet<String>();
		ObservableList<String> items=subjectCombo.getItems();
		String sub;
		for(LecturerQuestion lq:lqs)
		{
			//Add all LecturerQuestions to HashMap
			questions.put(lq.getText(), lq);
			
			//Fill subject ComboBox
			if(!subs.contains(lq.getSubject()))
			{
				sub=lq.getSubject();
				items.add(sub);
				subs.add(sub);
			}
		}
		
		//If a subject gets selected, add all corresponding questions to question combo
		subjectCombo.valueProperty().addListener(e->{
			ObservableList<String> its=questionCombo.getItems();
			its.clear();
			String subject=subjectCombo.getValue();
			for(LecturerQuestion lq:questions.values())
			{
				if(lq.getSubject().equals(subject))
					its.add(lq.getText());
			}
		});
	}
}