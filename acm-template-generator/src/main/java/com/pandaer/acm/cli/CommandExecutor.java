package com.pandaer.acm.cli;

import com.pandaer.acm.cli.command.ConfigCommand;
import com.pandaer.acm.cli.command.GenerateCommand;
import com.pandaer.acm.cli.command.ListCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "acm-template", description = "Acm代码生成器", mixinStandardHelpOptions = true,subcommands = {
        ConfigCommand.class, GenerateCommand.class, ListCommand.class
})
public class CommandExecutor implements Runnable {
    @Override
    public void run() {
        System.out.println("输入子命令，或者输入--help");
    }
}
