package gla;

import gla.commands.SsgBuildCommand;
import gla.commands.SsgServeCommand;
import gla.commands.VersionCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.RunAll;

/**
 * Class of application.
 */
public class App {

  public static final CommandLine cmd = new CommandLine(new ParentCommand());

  /**
   * main method.
   *
   * @param args main arguments
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      cmd.usage(System.out);
    }
    cmd.setExecutionStrategy(new RunAll());
    cmd.execute(args);
  }

  @Command(name = "ssg",
      subcommands = {
          SsgBuildCommand.class,
          SsgServeCommand.class,
          CommandLine.HelpCommand.class,
          VersionCommand.class
      },
      description = "Static site generator : transform markdown files into html files "
          + "to render a website.\n",
      headerHeading = "@|red,bold,underline Usage |@ :%n%n",
      commandListHeading = "@|red,bold,underline %nCommands |@ :%n%n",
      descriptionHeading = "@|red,bold,underline %nDescription |@ :%n%n")

  static class ParentCommand implements Runnable {

    @Override
    public void run() {
    }
  }
}
