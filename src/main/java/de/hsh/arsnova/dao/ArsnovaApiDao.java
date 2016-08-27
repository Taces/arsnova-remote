package de.hsh.arsnova.dao;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.hsh.arsnova.model.LecturerQuestion;
import de.hsh.arsnova.model.Session;
import javafx.util.Pair;

@Component
public class ArsnovaApiDao {

	private String sessionIdCookie;
	private int sessionId;

	private final String apiUrl="https://192.168.56.102/backend/";

	static{
		//TODO: Only for testing
		disableSslVerification();
	}

	public Pair<Integer, String> login(String username, String password)
	{
		HttpsURLConnection connection=null;
		OutputStreamWriter writer =null;
		try {
			String body = "type=" + URLEncoder.encode( "ldap", "UTF-8" ) + "&" +
					"user=" + URLEncoder.encode( username, "UTF-8" ) + "&" +
					"password=" + URLEncoder.encode( password, "UTF-8" );

			URL url = new URL( apiUrl+"auth/login" );
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod( "POST" );
			connection.setDoInput( true );
			connection.setDoOutput( true );
			connection.setUseCaches( false );
			connection.setRequestProperty( "Content-Type",
					"application/x-www-form-urlencoded" );
			connection.setRequestProperty( "Content-Length", String.valueOf(body.length()) );
			writer = new OutputStreamWriter( connection.getOutputStream() );
			writer.write( body );
			writer.flush();
			if(connection.getResponseCode()==HttpURLConnection.HTTP_OK)
			{
				Map<String, List<String>> header=connection.getHeaderFields();
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
										sessionIdCookie=s;
										break outMostFor;
									}
								}
							}
						}
					}
			}
			writer.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		finally{
			try {
				if(writer!=null)
					writer.close();
			} catch (IOException e) {}
		}
		if(connection!=null)
		{
			try {
				return new Pair<Integer, String>(connection.getResponseCode(), connection.getResponseMessage());
			} catch (IOException e) {e.printStackTrace();}
		}	
		return new Pair<Integer, String>(-1, "Unknown Error");
	}

	public Session[] getSessions(){
		try{
			String requestUrl=apiUrl+"session/";

			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("Cookie", sessionIdCookie);
			HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
			RestTemplate restTemplate=new RestTemplate();
			UriComponentsBuilder builder=UriComponentsBuilder.fromHttpUrl(requestUrl).queryParam("ownedonly", true);

			ResponseEntity<Session[]> response=restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, Session[].class);
			return response.getBody();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return new Session[0];
	}

	public LecturerQuestion[] getLecturerQuestion()
	{
		try{
			String requestUrl=apiUrl+"lecturerquestion/?sessionkey="+sessionId;
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("Cookie", sessionIdCookie);
			HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);
			RestTemplate restTemplate=new RestTemplate();
			ResponseEntity<LecturerQuestion[]> response=restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, LecturerQuestion[].class);
			if(response.getBody()!=null)
				return response.getBody();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return new LecturerQuestion[0];
	}

	public void setSession(int id)
	{
		this.sessionId=id;
	}

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
