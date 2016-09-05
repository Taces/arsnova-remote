package de.hsh.arsnova.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

/**
 * Taken from here: https://www.javacodegeeks.com/2013/03/javafx-2-with-spring.html
 *
 */

@Component
public class SpringFxmlLoader {

	private ApplicationContext applicationContext;
	
	 public Object load(String url)
	 {
		 try(InputStream fxmlStream =getClass().getClassLoader().getResourceAsStream(url)){
			 FXMLLoader loader=new FXMLLoader();
			 loader.setControllerFactory(new Callback<Class<?>, Object>(){
				@Override
				public Object call(Class<?> clazz) {
					return applicationContext.getBean(clazz);
				}
			 });
			 return loader.load(fxmlStream);
		 }
		 catch(IOException ex)
		 {
			 ex.printStackTrace();
		 }
		 return null;
	 }

	 public void setApplicationContext(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
	}
}
