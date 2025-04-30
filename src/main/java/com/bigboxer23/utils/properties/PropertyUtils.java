package com.bigboxer23.utils.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public class PropertyUtils {
	private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

	private static Properties props;

	public static String getProperty(String property) {
		if (props == null) {
			props = new Properties();
			try (InputStream inputStream =
					PropertyUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
				props.load(inputStream);
			} catch (IOException e) {
				logger.warn("Failed to load properties file", e);
				return null;
			}
		}
		return props.getProperty(property, System.getenv(property));
	}
}
