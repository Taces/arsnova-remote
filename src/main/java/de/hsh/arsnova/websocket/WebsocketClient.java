package de.hsh.arsnova.websocket;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import de.hsh.arsnova.util.NaiveSSLContext;

@Component
public class WebsocketClient{

	private static final Logger logger=Logger.getLogger(WebsocketClient.class);

	private String apiUrl=null;
	private String socketId;
	private String sessionIdCookie;
	private WebSocket websocket=null;
	
	@Autowired
	MessageHandler messageHandler;

	public void setApiUrl(String url)
	{
		this.apiUrl=url;
	}
	
	public void connect(String sessionIdCookie)
	{
		this.sessionIdCookie=sessionIdCookie;

		try {
			RestTemplate restTemplate=new RestTemplate();
			String wsUrl=restTemplate.getForObject(apiUrl+"socket/url", String.class).replace("http", "ws");
			wsUrl+="/socket.io/?EIO=3&transport=websocket";
			websocket = new WebSocketFactory()
					.setSSLContext(NaiveSSLContext.getInstance("TLS"))		//TODO: Only for testing
					.createSocket(wsUrl)
					.addListener(new WebSocketAdapter() {
						@Override
						public void onTextMessage(WebSocket ws, String message) {
							logger.debug("**** Received Message: "+message);
							if(message.startsWith("42"))	//Message|Ping
							{
								messageHandler.handleMessage(message.replaceFirst("42", ""));
							}
							else if(message.startsWith("0"))	//Open
							{
								handleSessionAssigment(message);
							}
						}
						@Override
						public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
						{
							logger.debug("**** Connected");
						}
						@Override
						public void onDisconnected(WebSocket websocket,
								WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
								boolean closedByServer) throws Exception
						{
							logger.debug("**** Disconnected");
						}
						@Override
						public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
							logger.debug("onPingFrame");
							
						}
						@Override
						public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
							logger.debug("onPongFrame");
							
						}
					})
					.connect();
		} catch (NoSuchAlgorithmException | WebSocketException | IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect()
	{
		if(websocket!=null)
			websocket.disconnect();
	}

	private void handleSessionAssigment(String message)
	{
		message=message.replaceFirst("0", "");
		JSONObject obj=new JSONObject(message);
		socketId=obj.getString("sid");
		JSONObject req=new JSONObject();
		req.put("session", socketId);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		requestHeaders.add("Cookie", sessionIdCookie);
		HttpEntity<?> requestEntity = new HttpEntity<>(req.toString(), requestHeaders);
		RestTemplate restTemplate=new RestTemplate();
		restTemplate.exchange(apiUrl+"/socket/assign/", HttpMethod.POST, requestEntity, String.class);
	}
	
	public void setSession(int keyword){
		String json="42[\"setSession\",{\"keyword\":\""+keyword+"\"}]";
		websocket.sendText(json);
	}
}
