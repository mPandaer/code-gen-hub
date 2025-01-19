package ${basePackage}.acm.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

/**
 * 获取原始项目文件列表
 */
@CommandLine.Command(name = "list", description = "获取原始项目文件列表", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {
    @Override
    public void run() {
        // 原始项目
        String originProjectDirPath = "${fileConfig.originProjectPath}";
        File originProject = new File(originProjectDirPath);

        // 输出文件列表信息
        List<File> files = FileUtil.loopFiles(originProject);
        for (File file : files) {
            String desc = String.format("文件名：%s, 文件路径: %s",file.getName(),file.getAbsolutePath());
            System.out.println(desc);
        }

    }
}
