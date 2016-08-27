package de.hsh.arsnova;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("de.hsh.arsnova")
@PropertySource("classpath:application.properties")
public class AppConfig {
	
}
