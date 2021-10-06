package gla.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A version.properties reader in order to display current app version
 */

public class VersionPropertiesReader {

  private final Properties properties = new Properties();
  private String propFile = "version.properties";

  /**
   * Reads property file from resources folder.
   *
   * @throws IOException - If file property not found
   */
  private void readProperties() throws IOException {
    InputStream inputStream =
        VersionPropertiesReader.class.getClassLoader().getResourceAsStream(propFile);

    if (inputStream == null) {
      throw new FileNotFoundException("Property file not found : " + propFile);
    }

    properties.load(inputStream);
    inputStream.close();
  }

  public VersionPropertiesReader(String file) throws IOException {
    propFile = file;
    this.readProperties();
  }

  /**
   * Instantiate using default version.properties file.
   *
   * @throws IOException - If file property not found
   */
  public VersionPropertiesReader() throws IOException {
    this.readProperties();
  }

  /**
   * Extract version for property file.
   *
   * @return - version
   */
  public String getVersion() {
    return properties.getProperty("version");
  }
}
