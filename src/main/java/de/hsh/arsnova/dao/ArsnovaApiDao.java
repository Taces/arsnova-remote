package de.hsh.arsnova.dao;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import de.hsh.arsnova.gui.controller.LoginController;
import de.hsh.arsnova.model.LecturerQuestion;
import de.hsh.arsnova.model.Session;

@Component
public class ArsnovaApiDao{

	private String sessionIdCookie;
	private int sessionId;

	//TODO: Add option menu/config
	private final String apiUrl="https://192.168.56.101/backend/";

	static{
		//TODO: Only for testing
		disableSslVerification();
	}

	public String getSessionId()
	{
		return sessionIdCookie;
	}
	
	public void setSessionId(String sessionIdCookie)
	{
		this.sessionIdCookie=sessionIdCookie;
	}

	public void login(String username, String password, LoginController loginController)
	{
		ListenableFuture<ResponseEntity<String>> futureEntity=null;
		try {
			String body = "type=" + URLEncoder.encode( "ldap", "UTF-8" ) + "&" +
					"user=" + URLEncoder.encode( username, "UTF-8" ) + "&" +
					"password=" + URLEncoder.encode( password, "UTF-8" );
			HttpHeaders headers=new HttpHeaders();
			headers.add("Content-Type", "application/x-www-form-urlencoded");
			HttpEntity<?> requestEntity = new HttpEntity<>(body,headers);
			AsyncRestTemplate restTemplate=new AsyncRestTemplate();
			futureEntity=restTemplate.exchange(apiUrl+"auth/login", HttpMethod.POST, requestEntity, String.class);
			futureEntity.addCallback(loginController::onLoginSuccess, loginController::onLoginError);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public Session[] getSessions(){
		String requestUrl=apiUrl+"session/?ownedonly=true";
		Object ss=sendRequest(requestUrl,  HttpMethod.GET, Session[].class);
		if(ss!=null)
			return (Session[]) ss;
		return new Session[0];
	}

	public LecturerQuestion[] getLecturerQuestions()
	{
		String requestUrl=apiUrl+"lecturerquestion/?sessionkey="+sessionId;
		Object lqs=sendRequest(requestUrl, HttpMethod.GET, LecturerQuestion[].class);
		if(lqs!=null)
			return (LecturerQuestion[])lqs;
		return new LecturerQuestion[0];
	}
	
	public LecturerQuestion getLecturerQuestion(String questionId)
	{
		String requestUrl=apiUrl+"/lecturerquestion/"+questionId;
		Object lqs=sendRequest(requestUrl, HttpMethod.GET, LecturerQuestion.class);
		if(lqs!=null)
			return (LecturerQuestion)lqs;
		return null;
	}

	public void startPiRound(String questionid, int duration)
	{
		String requestUrl=apiUrl+"lecturerquestion/"+questionid+"/startnewpiround?time="+duration;
		sendRequest(requestUrl, HttpMethod.POST, String.class);
	}
	
	public void cancelPiRound(String questionid)
	{
		String requestUrl=apiUrl+"lecturerquestion/"+questionid+"/canceldelayedpiround";
		sendRequest(requestUrl, HttpMethod.POST, String.class);
	}

	public void publishQuestion(boolean publish, String questionId)
	{
		String requestUrl=apiUrl+"lecturerquestion/"+questionId+"/disablevote?disable="+(publish ? 0:1);
		sendRequest(requestUrl, HttpMethod.POST, String.class);
	}

	private Object sendRequest(String url, HttpMethod method, Class<?> c)
	{
		try{
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("Cookie", sessionIdCookie);
			HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
			RestTemplate restTemplate=new RestTemplate();
			ResponseEntity<?> response=restTemplate.exchange(url, method, requestEntity, c);
			if(response.getBody()!=null)
				return response.getBody();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void setSession(int id)
	{
		this.sessionId=id;
	}

	//http://stackoverflow.com/questions/875467/java-client-certificates-over-https-ssl
	private static void disableSslVerification() {
		try
		{
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}
			};

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

}
