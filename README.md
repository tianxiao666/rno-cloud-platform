# 网优云服务平台 4.0

代码管理：http://svn.hgicreate.com/srcsvn/rno/rno-cloud-platform

## 安装所需的 Intellij IDEA 插件
### 1.安装 Lombok 插件
使用 Lombok 插件，可以方便的查看由 Lombok 生成的类定义和类的方法。

 1.打开 File > Settings... > Plugins > Browser repositories... 插件仓库页面。

2.输入 Lombok 关键字进行搜索。

3.选择 Lombok Plugin，点击 Install 按钮进行安装。

4.重启 Intellij IDEA 生效。

### 2.安装 MapStruct 插件

使用 MapStruct 插件，可以辅助编写 Domain 与 DTO 之间的 Mapper 类。

 1.打开 File > Settings... > Plugins > Browser repositories... 插件仓库页面。

2.输入 MapStruct 关键字进行搜索。

3.选择 MapStruct Support，点击 Install 按钮进行安装。

4.重启 Intellij IDEA 生效。

## 模块列表
|序号|功能模块|英文名称|所属网络|是否地图应用|
|----|----|----|----|----|
|1|LTE小区GIS呈现|lte-cell-gis|LTE|是|
|2|LTE小区配置管理|lte-cell-data|LTE|否|
|3|LTE邻区配置管理|lte-ncell-relation|LTE|否|
|4|地理场景管理|lte-geo-scene|LTE|否|
|5|时间场景管理|lte-time-scene|LTE|否|
|6|网格数据管理|lte-grid-data|LTE|否|
|7|网格地图展示|lte-grid-gis|LTE|是|
|8|路测数据管理|lte-dt-data|LTE|否|
|9|异常点问题分析优化|lte-dt-analysis|LTE|是|
|10|网络统计数据管理|lte-traffic-stats|LTE|否|
|11|业务热点覆盖分析优化|lte-traffic-analysis|LTE|是|
|12|KPI指标管理|lte-kpi-data|LTE|否|
|13|覆盖类KPI指标|lte-kpi-chart|LTE|否|
|14|性能类KPI指标|lte-kpi-query|LTE|否|

## 系统部署
部署命令：
使用持续集成工具Jenkins进行自动化构建和部署。

运行命令：
<pre> // TODO </pre>
