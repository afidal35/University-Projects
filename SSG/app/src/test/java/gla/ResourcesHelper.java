package gla;

import gla.files.html.HtmlObj;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class ResourcesHelper {

  public static void copyFolder(Path src, Path dest) throws IOException {
    try (Stream<Path> stream = Files.walk(src)) {
      stream.forEach(source -> {
        try {
          Files.copy(source, dest.resolve(src.relativize(source)),
              StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  public static void copyResourceFile(String resource, Path dest) throws IOException {
    String newContent = Files.readString(getResourcePath(resource));
    Files.writeString(dest, newContent);
  }

  public static Path copyResourcesFolder(String pathResource, Path tmpPath) throws IOException {
    Path srcPath = getResourcePath(pathResource);
    Path dstPath = tmpPath.resolve("input");
    copyFolder(srcPath, dstPath);
    return dstPath;
  }

  public static URL gerResourceUrl(String path) {
    return ResourcesHelper.class.getResource(path);
  }

  private static String getResourceString(String path) throws IOException {
    return Files.readString(getResourcePath(path));
  }

  public static HtmlObj getResourceHtml(String path) throws IOException {
    return new HtmlObj(getResourceString(path));
  }

  public static Path getResourcePath(String path) {
    return Path.of(gerResourceUrl(path).getPath());
  }

  public static TemplateFolder getTemplate(String path) {
    return new TemplateFolder(getResourcePath(path));
  }



}
