package gla.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

public class VersionPropertiesReaderTest {
  @Test
  public void throwIOException() {
    try {
      new VersionPropertiesReader("notafile");
      fail("Version properties reader didn't throw exception ");
    } catch (IOException e) {
      // Do nothing, expected
    }
  }

  @Test
  public void displayVersion() {
    try {
      VersionPropertiesReader version = new VersionPropertiesReader();
      assertTrue(StringUtils.isNotBlank(version.getVersion()));
    } catch (IOException e) {
      fail("File not found");
    }

  }
}
