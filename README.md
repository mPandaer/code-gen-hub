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

### 实现代码生成的能力

我的设计是这样的，根据生成类型的不同，我将生成器分为两个，一个是静态文件生成器，一个是动态文件生成器，静态文件生成器的代码生成的实现很简单，就是复制文件。而动态代码生成器利用了Freemarker模板引擎以及动态模板文件，根据用户输入的数据模型，生成定制化之后的代码文件。

最后我利用一个额外的类，`CodeGenerator` 来实现对上面这两种代码生成器的包装，这样用户只需要传递三个参数，一个是数据模型，一个是原始项目所在的目录，另一个是生成的项目所在的目录。

我的实现思路：

1. 遍历原始项目，获取文件列表
2. 根据文件的生成类型，交给不同的代码生成器（静态文件交给静态代码生成器，动态文件交给动态代码生成器）
3. 输出生成完毕的提示

### 实现命令行输入的能力

...

## 阶段二  实现代码生成器制作工具

...

## 阶段三   代码生成器制作工具云化

...

## 阶段四  其他核心模块完善

...

## 项目总结

...