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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

@Controller
public class SessionManagerController implements Initializable{

	private HashMap<String, LecturerQuestion> questions;

	private Timeline countdownTimeline;

	private final String STATUS_UNKNOWN="Unknown";
	private final String STATUS_ACTIVE="Active";
	private final String STATUS_PI_ACTIVE="PI round active";
	private final String STATUS_INACTIVE="Inactive";

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
	Button startPiRoundButton;

	@FXML
	Button openQuestionButton;

	@FXML
	HBox statusBox;

	@FXML
	Label statusLabel;

	@FXML
	protected void goBack(ActionEvent e)
	{
		screensManager.showSessionSelectionScreen();
	}

	@FXML
	protected void startPiRound(ActionEvent e)
	{
		LecturerQuestion lq=getCurrentlySelectedQuestion();
		if(lq==null)
			return;
		if(lq.isPiRoundActive())
			arsnovaApi.cancelPiRound(lq.get_id());
		else
			arsnovaApi.startPiRound(lq.get_id(),Integer.valueOf(countdownTextfield.getText()));
	}

	@FXML
	protected void openQuestion(ActionEvent e)
	{
		LecturerQuestion lq=getCurrentlySelectedQuestion();
		if(lq==null)
			return;
		if(lq.isVotingDisabled())
			arsnovaApi.publishQuestion(true, lq.get_id());
		else
			arsnovaApi.publishQuestion(false, lq.get_id());
	}

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		countdownTimeline=new Timeline();

		questions=new HashMap<String, LecturerQuestion>();
		LecturerQuestion[] lqs=arsnovaApi.getLecturerQuestions();
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

		questionCombo.valueProperty().addListener(e->{
			if(questionCombo.getValue()==null)
			{
				statusBox.setVisible(false);
				return;
			}
			statusBox.setVisible(true);
			statusLabel.setText(STATUS_UNKNOWN);
			fetchQuestionUpdate(getCurrentlySelectedQuestion().get_id());
			updateStatus();
		});

		//Only allow numbers in textfield
		//http://stackoverflow.com/questions/7555564
		countdownTextfield.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					countdownTextfield.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
	}

	public void receivedEvent(String eventName, String questionId)
	{
		boolean updateQuestion=true;
		switch(eventName)
		{
		case "endPiRound":
		case "cancelPiRound":	//Both cases can be treatet alike
		{
			openQuestionButton.setDisable(false);
			countdownTimeline.stop();
			countdownTextfield.setEditable(true);
			countdownTextfield.setText("30");
			break;
		}
		case "startDelayedPiRound":
		{
			countdownTextfield.setEditable(false);
			openQuestionButton.setDisable(true);
			break;
		}
		case "unlockVote":
		{
			startPiRoundButton.setDisable(true);
			break;
		}
		case "lockVote":
		{
			startPiRoundButton.setDisable(true);
			break;
		}
		default:
		{
			System.out.println("################ Unhandled Event:"+eventName);
			updateQuestion=false;
			break;
		}
		}
		if(updateQuestion)
		{
			fetchQuestionUpdate(questionId);
			updateStatus();
		}
	}

	public void fetchQuestionUpdate(String questionId)
	{
		LecturerQuestion lq=arsnovaApi.getLecturerQuestion(questionId);
		questions.put(lq.getText(), lq);
	}

	private void updateStatus()
	{
		Platform.runLater(()->{
			LecturerQuestion lq=getCurrentlySelectedQuestion();
			if(lq.isPiRoundActive())
			{
				statusLabel.setText(STATUS_PI_ACTIVE);
				startPiRoundButton.setText("Cancel round");
				countdownTimeline.stop();
				countdownTimeline.getKeyFrames().clear();
				countdownTimeline.setCycleCount((int) ((lq.getPiRoundEndTime()-lq.getPiRoundStartTime())/1000));
				countdownTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), e->{
					long restTime=lq.getPiRoundEndTime()-System.currentTimeMillis();
					restTime/=1000;
					countdownTextfield.setText(""+restTime);
				}));
				countdownTextfield.setText(""+(Integer.valueOf(countdownTextfield.getText())-1));
				countdownTimeline.play();
			}	
			else if(lq.isVotingDisabled())
			{
				statusLabel.setText(STATUS_INACTIVE);
				openQuestionButton.setText("Open question");
				startPiRoundButton.setText("Start round");
			}	
			else 
			{
				statusLabel.setText(STATUS_ACTIVE);
				openQuestionButton.setText("Close question");
			}
		});
	}

	private LecturerQuestion getCurrentlySelectedQuestion()
	{
		if(questionCombo.getValue()==null)
			return null;
		return questions.get(questionCombo.getValue());
	}
}