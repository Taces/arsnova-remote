package de.hsh.arsnova.websocket;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hsh.arsnova.gui.controller.SessionManagerController;

@Component
public class MessageHandler {

	@Autowired
	SessionManagerController sessionManager;

	public void handleMessage(String message)
	{
		JSONArray json=new JSONArray(message);
		String eventName=(String)json.get(0);
		switch(eventName)
		{
		case "startDelayedPiRound":
		case "endPiRound":
		case "unlockVote":
		case "lockVote":
		{
			sessionManager.receivedEvent(eventName, ((JSONObject)json.get(1)).getString("_id"));
			break;
		}
		case "cancelPiRound":
		{
			sessionManager.receivedEvent(eventName, json.getString(1));
			break;
		}
		case "activeUserCountData":
			break;
		default:
		{
			System.out.println("################ Unhandled Event:"+eventName);
			break;
		}
		}

	}

}
