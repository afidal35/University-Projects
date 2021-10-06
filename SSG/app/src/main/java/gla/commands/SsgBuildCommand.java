package gla.commands;

import gla.HtmlSiteBuilder;
import gla.Md2HtmlRenderer;
import gla.folders.OutputFolder;
import java.nio.file.Path;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Ssg build command class.
 */
@Command(
    name = "build",
    description = "Convert markdown files (commonmark spec) into HTML files.\n"
        + "When specified with no options build from the current directory"
        + " into the default output directory [_output].\n",
    headerHeading = "@|red,bold,underline Usage |@ :%n%n",
    descriptionHeading = "@|red,bold,underline %nDescription |@ :%n%n",
    parameterListHeading = "@|red,bold,underline %nParameters |@ :%n%n",
    optionListHeading = "@|red,bold,underline %nOptions |@ :%n%n"
)
public class SsgBuildCommand extends Thread {

  @ArgGroup
  private final Md2HtmlOptionExclusive exclusive1 = new Md2HtmlOptionExclusive();

  @Option(
      names = {"-o", "--output-dir"},
      description = "Store built file(s) to the specified directory.",
      defaultValue = OutputFolder.DEFAULT_FOLDER_NAME
  )
  protected String output;

  @Option(
      names = {"-w", "--watch"},
      defaultValue = "false",
      description = "Watch file changes and recompile if needed."
  )
  protected boolean watch;

  @Option(
      names = {"-r", "--rebuild-all"},
      defaultValue = "false",
      description = "Rebuild all the site."
  )
  protected boolean rebuild;


  @Option(
      names = {"--jobs"},
      description = "Number max of job",
      defaultValue = "-1"
  )
  protected int jobs;
  /*
  @Option(
      names = {"-v", "--verbose"},
      defaultValue = "false",
      description = "Verbose mode, print messages when building."
  )
  boolean verbose;
  */

  @Override
  public void run() {
    try {
      if (exclusive1.file != null) {
        Md2HtmlRenderer renderer = new Md2HtmlRenderer(Path.of(output));
        renderer.renderHtmlFile(exclusive1.file);
      } else {
        HtmlSiteBuilder builder = new HtmlSiteBuilder(exclusive1.input, output)
            .setRebuild(rebuild)
            .setJobs(jobs);
        builder.build();
        if (watch) {
          builder.getWatchdog().start();
        }
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

}