package ${basePackage}.acm.cli;

import ${basePackage}.acm.cli.command.ConfigCommand;
import ${basePackage}.acm.cli.command.GenerateCommand;
import ${basePackage}.acm.cli.command.JsonGenerateCommand;
import ${basePackage}.acm.cli.command.ListCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "${name}", description = "${description}", mixinStandardHelpOptions = true,subcommands = {
        ConfigCommand.class, GenerateCommand.class, ListCommand.class, JsonGenerateCommand.class
})
public class CommandExecutor implements Runnable {
    @Override
    public void run() {
        System.out.println("输入子命令，或者输入--help");
    }
}
