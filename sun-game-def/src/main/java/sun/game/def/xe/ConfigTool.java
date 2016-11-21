package sun.game.def.xe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigTool {
	private static Logger logger = LoggerFactory.getLogger(ConfigTool.class);

	private static ConfigTool configTool = new ConfigTool();
	private static Properties properties;

	// 初始化配置文件信息
	private ConfigTool() {
		load();
	}

	/**
	 * 重载系统文件
	 */
	public static void load() {
		InputStream inputStream = null;
		try {
			// 加载系统配置文件
			inputStream = ConfigTool.class.getClassLoader().getResourceAsStream("xml.properties");
			properties = new Properties();
			properties.load(inputStream);
			logger.info("reload system properties success!!!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static ConfigTool instance() {
		return configTool;
	}

	/**
	 * 
	 * @return Properties 系统信息
	 * @throws
	 */
	public Properties getSystemProperties() {
		if (properties == null || properties.size() == 0) {
			load();
		}
		return properties;
	}

}
