# 代码生成器共享平台

## 项目介绍

### 选择原因

这是我的毕业设计，同时也是鱼皮哥的一个项目，之所以选择这个项目完全就是看这个项目名字比较高级仅此而已罢了。

### 正式的介绍

该项目允许用户上传，下载，以及在线制作代码生成器，并使用代码生成器提高开发效率。

### 技术选型

前端：

1. React
2. Ant Design Pro

后端：

1. SpringBoot
2. MyBatis-Plus
3. MySQL
4. Maven
5. Picocli
6. FreeMarker
7. Caffeine + Redis
8. OSS
9. Vert.x
10. JMeter

## 阶段一  手写代码生成器

阶段一，更像是一个补齐前置知识的阶段，通过手写一个简单的代码生成器来熟悉代码生成器的核心功能即代码生成 + 命令行输入，然后利用代码将代码生成的步骤实现出来，以及进一步思考如何制作动态模板文件。

### 1.1 实现代码生成的能力

我的设计是这样的，根据生成类型的不同，我将生成器分为两个，一个是静态文件生成器，一个是动态文件生成器，静态文件生成器的代码生成的实现很简单，就是复制文件。而动态代码生成器利用了Freemarker模板引擎以及动态模板文件，根据用户输入的数据模型，生成定制化之后的代码文件。

最后我利用一个额外的类，`CodeGenerator` 来实现对上面这两种代码生成器的包装，这样用户只需要传递三个参数，一个是数据模型，一个是原始项目所在的目录，另一个是生成的项目所在的目录。

我的实现思路：

1. 遍历原始项目，获取文件列表
2. 根据文件的生成类型，交给不同的代码生成器（静态文件交给静态代码生成器，动态文件交给动态代码生成器）
3. 输出生成完毕的提示

### 1.2 实现命令行输入的能力

对于命令行输入的能力，对于这种通用的命令行开发，肯定有人已经将这段逻辑封装成了库或者框架，所以我们的第一步就是先调研一下，有哪些开源的支持命令行开发的代码库和框架，最简单的方法就是问AI，AI的回答如下：

1. Apache Commons CLI
2. [**Picocli**](https://github.com/remkop/picocli)
3. JCommander
4. Spring Shell
5. Airline
6. JLine

经过调研和尝试，我们选择picocli,主要考虑的是，开源项目是否活跃，文档是否丰富完整，社区的支持如何，而Picocli这个开源项目非常活跃，文档写的也非常丰富。

选择好命令行开发的框架之后，就是将这个框架集成到我们的简单代码生成器中，让用户输入数据模型参数的值。由于我们使用Java开发，所以运行程序时，需要 `java -jar xxxx.jar arg1 arg2 arg3` 这样的命令太长了，所以我们可以将这条命令封装成命令行脚本，方便用户使用。

## 阶段二  实现代码生成器制作工具

阶段二是整个项目的核心业务逻辑，阶段二被分为三个小阶段，内容如下：

2.1：实现代码生成器制作工具核心流程

2.2：增强元信息配置文件的能力

2.3：增加模版制作工具的能力

### 2.1 实现代码生成器制作工具核心流程

这个阶段主要是将阶段一 手写代码生成器的流程用代码实现出来，手写代码生成的流程

1. 模版文件的制作
2. 代码文件的生成
3. 构建Jar包
4. 封装命令行脚本文件

第一步，模版文件的制作，我们需要模板文件，所以在这个2.1的阶段，我们仍然是手动制作模板文件，所以相对于这一步没有做，因为它最复杂。

第二步，代码文件的生成，利用第一步制作好的模板文件，以及元信息配置，生成出代码生成器需要的代码文件（包括代码生成能力的代码，命令行输入相关的代码，数据模型类）

第三步，利用制作工具构建Jar包，对应到代码中就是`ProcessBuilder`,以及`Process`，然后利用系统中已经存在的Maven工具构建Jar包

第四步，封装命令行脚本文件，因为Java的运行，需要 `java -jar xxx.jar arg1 arg2` 所以为了简化用户的使用，我们将这个命令封装成一个脚本文件。

### 制作工具优化

我们在2.1阶段实现的代码生成器制作工具比较简陋，基础。所以我们需要先做一些优化以及重构，然后再继续后面的工作。就和TDD的思想类似，红-绿-重构。如果代码的可读性不高时，代码比较混乱时，就需要重构代码，让代码保持整洁了。我们主要从四个方面来优化代码

1. 可移植性优化
2. 功能优化
3. 健壮性优化
4. 可扩展性优化

做可移植性优化的目的在于，我们制作好的代码生成器可以在任何目录下都可以运行。具体的做法就是让原始项目文件复制一份到代码生成器内部。

做功能优化，主要是完善一下代码生成器制作工具的能力，比如根据元信息配置文件自动生成README.md文件，以及生成精简版本的产物包。

做健壮性优化，主要的目的在于可以保证用户的非法输入不会导致我们程序的崩溃，主要的工作是对元信息配置文件做了校验工作

做可扩展性优化，主要的目的在于方便后续完善功能时，易于开发，主要的内容是 减少代码中的**硬编码**，将制作流程使用模板方法设计模式固定下来，并提供一个默认实现`DefaultGeneratorMaker`。

### 2.2 增强元信息配置文件的能力

在2.2阶段，我们增强了制作出的代码生成器的通用能力，在2.1阶段制作出来的代码生成器只支持数据模型控制代码内容的生成，功能有限。而这个阶段就是为了让我们制作出来的代码生成器具备更多的功能，比如：

1. 一个数据模型参数控制文件的生成
2. 一个模型参数控制一组文件的生成
3. 模型参数支持分组
4. 模型除了支持分组外，还支持使用另外一个模型参数控制这组模型参数

在这个阶段，我们通过修改元信息配置文件，增加一些字段，比如`groupKey` `condition`，以及在代码生成器的模板文件中修改部分逻辑使得我们能够实现上面的四个功能。但是这个阶段仍然没有实现**原始工程模板的制作**。这个工作安排到2.3阶段完成。

### 2.3 增加模版制作工具的能力

这一期主要是完善代码生成器制作工具的能力，说的再具体点就是增加一个模板文件的制作模块。这里需要额外注意，模板文件制作模块，并不能取代人工制作模板文件，只能提高人工制作模板文件的效率。以此为目的出发，我们需要实现下面这些能力

1. 实现整个模板文件制作的流程
2. 实现工作空间，防止并发制作时，文件混乱
3. 实现有状态制作，进一步支持连续制作，分步制作
4. 实现一次制作出多个模板文件
5. 实现文件过滤的功能
6. 实现文件分组
7. 实现模型分组

这里我们使用了先跑通，再完善，再完美的思路实现了复杂的业务流程。在一次一次的迭代过程中，我们需要不断的进行**重构-测试**。

针对软件设计，在文件过滤的模块，文件过滤的需求是**在指定的范围应用指定的过滤规则**。范围我们分为：文件名，文件内容。过滤规则我们分为：包含，前缀匹配，后缀匹配，正则匹配，相等。我们将范围和规则都定义为枚举，因为每一个具体的枚举实例都可以是一个匿名内部类即类与对象在一起定义。所以我利用抽象方法，利用多态的特性，避免了复杂的if-else判断。具体设计如下：

```java
package com.pandaer.maker.template.enums;


import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 文件过滤范围
 */
public enum FileFilterRange {

    FILE_NAME("文件名","fileName"){
        @Override
        public String getContent(File file) {
            return file.getName();
        }
    },
    FILE_CONTENT("文件内容","fileContent") {
        @Override
        public String getContent(File file) {
            return FileUtil.readUtf8String(file);
        }
    };

    public final String desc;
    public final String value;

    FileFilterRange(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }

    public abstract String getContent(File file);
}

```

```java
package com.pandaer.maker.template.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 文件过滤规则
 */
public enum FileFilterRule {

    CONTAINS("包含","contains") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.contains(value);
        }
    },
    STARTS_WITH("前缀匹配","startsWith") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.startsWith(value);
        }
    },
    ENDS_WITH("后缀匹配","endsWith") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.endsWith(value);
        }
    },
    REGEX("正则匹配","regex") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.matches(value);
        }
    },
    EQUALS("相等","equals") {
        @Override
        public boolean apply(String content, String value) {
            if (StrUtil.hasBlank(content,value)) {
                return false;
            }
            return content.equals(value);
        }
    },
    ;

    public final String desc;
    public final String value;

    FileFilterRule(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }

    public abstract boolean apply(String content,String value);
}

```



到此，整个代码生成器制作工具的核心能力开发完成，我们可以发现，应用“先跑通，再完善，再完美”这样的需求拆分思路，可以让我们比较轻松的实现功能。这其实也是一种分治的思想，在某个时刻只关注一个小功能，最后将这些小功能组合实现一个复杂的，庞大的功能。

### 代码生成器制作工具功能优化

我们通过制作一个SpringBootInit初始化项目模板项目，测试验证了我们的代码生成器制作工具可以应对复杂的项目。验证的功能如下：

1. 测试一次制作多个模板文件
2. 测试文件分组能力
3. 测试条件生成单文件的能力
4. 测试一个参数控制一组模型参数
5. 测试数据模型分组的能力
6. 测试一个模型参数控制文件和内容（这里是半自动，文件的控制可以借助我们的工具，但是具体的代码内容，过于定制化只能手动完成）

一些不足：

1. 当我们在替换整体包名的时候，只能替换文件内容中定义的 `package xxx.xxx`而对应的目录没有被替换
2. 我们制作好的代码生成器，存在一个参数控制一组参数的情况时，如果这个参数的输入不是最后一个，那么就会导致交互体验不是很好，如下图：
   ![image-20250123191928106](D:\code\code-gen-hub\images\image-20250123191928106.png)

到此，整个代码生成器制作工具的全部逻辑都实现完成，接下来就是将代码生成器“云”化，即通过在线的方式使用我们的代码生成器。

## 阶段三   代码生成器制作工具云化

...

## 阶段四  其他核心模块完善

...

## 项目总结

...