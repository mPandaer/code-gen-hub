package ${basePackage}.acm.model;

import lombok.Data;

<#macro generateModel indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent} * ${modelInfo.description}
${indent} */
</#if>
${indent}public ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;

</#macro>




@Data
public class DataModel {

<#list modelConfig.models as modelInfo>

<#if modelInfo.groupKey??>
    /**
     * ${modelInfo.groupName}
     */
    public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();

    @Data
    public static class ${modelInfo.type} {
    <#list modelInfo.models as subModelInfo>
        <@generateModel indent="        " modelInfo=subModelInfo/>
    </#list>

    }
<#else>
    <@generateModel indent="    " modelInfo=modelInfo/>
</#if>

</#list>

}
