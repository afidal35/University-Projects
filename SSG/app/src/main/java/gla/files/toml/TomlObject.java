package gla.files.toml;

import gla.exceptions.templates.NoMedataWithKeyException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

/**
 * Toml Object.
 */
public class TomlObject {

  private final TomlTable table;

  /**
   * Parse Toml with content string.
   *
   * @param content content to parse
   */
  public TomlObject(String content) {
    table = Toml.parse(content);
  }

  /**
   * Parse toml with file to parse.
   *
   * @param path path of file
   * @throws IOException IO error to load file.
   */
  public TomlObject(Path path) throws IOException {
    table = Toml.parse(path);
  }

  /**
   * Convert deep Toml Array to list of object.
   *
   * @param array array toml to convert
   * @return List object
   */
  private static List<Object> deepTomlArrayToList(TomlArray array) {
    return array.toList().stream()
        .map(TomlObject::deepConvert)
        .collect(Collectors.toList());
  }

  /**
   * Convert deep Toml Table to Map of object.
   *
   * @param table table toml to convert
   * @return Map String object
   */
  private static Map<String, Object> deepTomlTableToList(TomlTable table) {
    return table.toMap().entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            (entry) -> deepConvert(entry.getValue())));
  }

  /**
   * Deep Convert object of type toml if it is.
   *
   * @param object object to check.
   * @return Object
   */
  private static Object deepConvert(Object object) {
    if (object == null) {
      return null;
    } else if (object instanceof TomlArray) {
      return deepTomlArrayToList((TomlArray) object);
    } else if (object instanceof TomlTable) {
      return deepTomlTableToList((TomlTable) object);
    }
    return object;
  }

  /**
   * To get map in table.
   *
   * @return Map
   */
  public Map<String, Object> toMap() {
    return deepTomlTableToList(table);
  }

  /**
   * Getter value.
   *
   * @param key Key use to get value
   * @return String value
   * @throws NoMedataWithKeyException throw if data not found
   */
  public String get(String key) throws NoMedataWithKeyException {
    Object object = table.get(key);
    if (object == null) {
      throw new NoMedataWithKeyException(key);
    }
    return object.toString();
  }

  /**
   * Getter value.
   *
   * @param key          Key used to get value
   * @param defaultValue default value if not founded
   * @return String value
   */
  public String get(String key, String defaultValue) {
    try {
      return get(key);
    } catch (NoMedataWithKeyException e) {
      return defaultValue;
    }
  }

  /**
   * getter Table TOML.
   *
   * @return TomlTable
   */
  public TomlTable toTable() {
    return table;
  }

  /**
   * Size of table.
   *
   * @return int
   */
  public int size() {
    return table.size();
  }

  @Override
  public String toString() {
    return "TomlObject{" + table + '}';
  }
}
