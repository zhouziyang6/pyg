<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Freemarker 测试</title>
</head>
<body>
<#--这是 freemarker 注释，不会输出到文件中-->
<h1>${name}；${message}</h1>





<#--assign-->
<#--此指令用于在页面上定义一个变量。语法：<#assign name=value>-->
<#--简单类型-->
<#assign linkman="黑马"/>
联系人: ${linkman}
<br>
<#--对象类型-->
<#assign info={"mobile":"17600000000","address":"广州天河区吉山村"}/>
电话: ${info.mobile}, 联系地址: ${info.address}
<br>


<#--语法：<#include path>
    说明：path 参数可以是如 "foo.ftl" 和 "../foo.ftl" 一样的相对路径，或者是如"/foo.ftl" 这样的绝对路径。
-->
<#--include-->
<#include "header.ftl"/>
<br>

<#--语法：
<#if condition>
...
<#elseif condition2>
...
<#elseif condition3>
...
...
<#else>
...
</#if>
这里：
condition ， condition2 等：表达式将被计算成布尔值。

关键字：gt ：比较运算符“大于”；gte ：比较运算符“大于或等于”；lt ：比较
运算符“小于”；lte ：比较运算符“小于或等于”

-->

<#--if-->
<#assign test=true/>
<#if test>
    test的值为true
<#else>
    test的值为false
</#if>
<br>

<#--语法：
<#list sequence as item>
...
</#list>
这里：
sequence ：表达式将被算作序列或集合
item ：循环变量（不是表达式）的名称
如果想在循环中得到索引，使用循环变量+_index 就可以得到。如上述语法中
则可以使用 item_index 可以得到循环变量
-->
<#--list-->
<#list goodsList as goods>
    编号: ${goods_index},名称为: ${goods.name},价格为: ${goods.price}
</#list>
<br>



<#--内建函数-->
<hr>
<#--使用 size 函数来实现对于集合大小的获取-->
<hr>
<#--获取集合总记录数-->
总共${goodsList?size}条记录

<#--转换 JSON 字符串为对象-->
<#--将对象转换为 JSON 字符串-->
<#--可以使用 eval 将 json 字符串转换为对象-->
<#assign str="{'id':123,'text':'itcast'}"/>
<#assign jsonObj=str?eval/>
id为: ${jsonObj.id}; test为: ${jsonObj.text}
<br>
<#--日期格式化-->
当前日期: ${today?date}<br>
当期时间: ${today?time}<br>
当前日期+时间: ${today?datetime}<br>
格式化显示当前日期和时间: ${today?string('yyyy年MM月dd日 HH:mm:ss')}<br>


<#--数值显示处理-->
number:${number}; number?c=${number?c};<br><#--一个有分割,一个直接输出没有分割-->

<#--
    空值处理:
        在 FreeMarker 中对于空值必须手动处理
        在插值中处理空值：或者${emp.name!} 表示 name 为空时什么都不显示
        ${emp.name!(“名字为空”)} 表示 name 为空时显示 名字为空
        ${(emp.company.name)!} 表示如果 company 对象为空则什么都不显示，!只用
        在最近的那个属性判断；所以如果遇上有自定义类型（导航）属性时，需要使
        用括号
        ${bool???string} 表示：首先??表示判断 bool 变量是否存在，存在返回 true 否
        则 false，然后对返回的值调用其内置函数 string
        <#if str??> 表示去判断 str 变量是否存在，存在则 true，不存在为 false
-->

<#--空值的处理-->
${stw!"str空值的默认显示"}<br>
<#--判断变量是否存在-->
<#if stw??>
str变量存在
    <#else>
str变量不存在
</#if>


</body>
</html>
