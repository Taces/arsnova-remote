package de.hsh.arsnova.gui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import de.hsh.arsnova.model.Answer;
import de.hsh.arsnova.model.PossibleAnswer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

@Component
public class AnswerPanelController implements Initializable{

	@FXML
	BarChart<String, Number> barChart;

	@FXML
	CategoryAxis xAxis;

	@FXML
	NumberAxis yAxis;

	@FXML
	Button bla;

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		xAxis.setLabel("Answer");       
		yAxis.setLabel("# Votes");
	}

	public void updateChart(String question, PossibleAnswer possibleAnswers[], Answer[] answers)
	{
		barChart.setTitle(question);
		barChart.setAnimated(false);
		HashMap<String, Integer> round1=new HashMap<String, Integer>();
		HashMap<String, Integer> round2=new HashMap<String, Integer>();
		for(PossibleAnswer pb:possibleAnswers)
		{
			round1.put(pb.getText(), 0);
			round2.put(pb.getText(), 0);
		}
		if(answers.length>0)
		{
			if(answers[0].getAbstentionCount()>0)
			{
				round1.put("Abstention", 0);
				round2.put("Abstention", 0);
			}
		}
		boolean onlyOnePi=true;
		for(Answer a:answers)
		{
			String key=a.getAnswerText()!=null?a.getAnswerText():"Abstention";
			if(a.getPiRound()==1)
				round1.put(key, a.getAnswerCount());
			else
			{
				round2.put(key, a.getAnswerCount());
				onlyOnePi=false;
			}
		}
		XYChart.Series<String, Number> chartRound1 = new XYChart.Series<String, Number>();
		chartRound1.setName("PiRound 1");
		XYChart.Series<String, Number> chartRound2 = new XYChart.Series<String, Number>();
		chartRound2.setName("PiRound 2");
		for(String key:round1.keySet())
		{
			chartRound1.getData().add(new XYChart.Data<String, Number>(key, round1.get(key)));
			chartRound2.getData().add(new XYChart.Data<String, Number>(key, round2.get(key)));
		}
		barChart.getData().add(chartRound1);
		if(!onlyOnePi)
			barChart.getData().add(chartRound2);
	}
}
