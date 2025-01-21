# ${name}

> ${description}
>
> 作者：${author}
>

可以通过命令行交互式输入的方式动态生成想要的项目代码

## 使用说明

执行项目根目录下的脚本文件：

```
generator <命令> <选项参数>
```

示例命令：

```
generator generate <#list modelConfig.models as modelInfo><#if modelInfo.fieldName??>--${modelInfo.fieldName} </#if></#list>
```

## 参数说明

<#list modelConfig.models as modelInfo>
<#if modelInfo.groupKey??>
${modelInfo?index + 1}）${modelInfo.groupKey}

- 类型：${modelInfo.type}

<#if modelInfo.groupName??>
- 描述：${modelInfo.groupName}
</#if>

<#if modelInfo.defaultValue??>
    - 默认值：${modelInfo.defaultValue?c}
</#if>

<#if modelInfo.abbr??>
    - 缩写： -${modelInfo.abbr}
</#if>

<#else>
${modelInfo?index + 1}）${modelInfo.fieldName}

- 类型：${modelInfo.type}

<#if modelInfo.description??>
    - 描述：${modelInfo.description}
</#if>

<#if modelInfo.defaultValue??>
    - 默认值：${modelInfo.defaultValue?c}
</#if>

<#if modelInfo.abbr??>
    - 缩写： -${modelInfo.abbr}
</#if>

</#if>







</#list>