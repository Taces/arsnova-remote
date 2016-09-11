package de.hsh.arsnova.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.context.ApplicationContext;

import de.hsh.arsnova.dao.ArsnovaApiDao;
import de.hsh.arsnova.websocket.WebsocketClient;

public class ExternalConfig {

	private static Properties properties;
	private static ApplicationContext appContext;
	private static final String CONFIG_FILE="config.cfg";
	
	public static final String API_URL="arsnova.api-url";

	public static void initConfig()
	{
		try{
			File f=new File(CONFIG_FILE);
			if(!f.exists())
				createConfig();
			properties = new Properties();
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(CONFIG_FILE));
			properties.load(stream);
			stream.close();
		}
		catch(IOException e)
		{
			//Should not happen
			e.printStackTrace();
		}
	}
	
	public static void injectProperties()
	{
		appContext.getBean(WebsocketClient.class).setApiUrl(properties.getProperty(API_URL));
		appContext.getBean(ArsnovaApiDao.class).setApiUrl(properties.getProperty(API_URL));
	}

	private static void createConfig() throws IOException
	{
		properties = new Properties();
		properties.setProperty(API_URL, "https://arsnova.eu/api/");
		saveConfig();
	}
	
	public static void setAppContext(ApplicationContext ac)
	{
		appContext=ac;
	}

	public static String getProperty(String key)
	{
		return properties.getProperty(key);
	}
	
	public static void setPropertie(String key, String value)
	{
		properties.setProperty(key, value);
	}

	public static void saveConfig()
	{
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(CONFIG_FILE));
			properties.store(bos, null);
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
