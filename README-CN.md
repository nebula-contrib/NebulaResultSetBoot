<p align="center">
  <br>  <a href="README-CN.md">English</a> | 中文
  <br>ResultSetBoot for Nebula ResultSet<br>
</p>

# NebulaResultSetBoot

## 介绍
NebulaResultSetBoot是一个帮助开发者将Nebula客户端的查询结果转为JSON的小工具。

## 安装
clone源码到本地
```shell
mvn clean install
```
```
<dependency>
    <groupId>com.github.dragonchu</groupId>
    <artifactId>NebulaResultSetBoot</artifactId>
    <version>1.0.0</version>
</dependency>
```
也可以直接将源码中的rsboot包复制到自己项目中
## 使用方式
### ResultBootSet
```
ResultBoot.wrap(resultSet).toJson();
ResultBoot.wrap(resultSet).withColumnName().toJson();
ResultBoot.wrap(resultSet).rowOriented().withColumnName().toJson();
```
toJson(): 转为Json对象，使用fastjson进行序列化，默认整个结果集整合在一起

withColumnName(): 结果集中显示列名，添加该选项默认按列聚集

rowOriented(): 按行聚集

matrixStyle(): 按列聚集

```
Example 1: match p=(v:player) return p limit 1;
Result json（withColumnName) -> {"p":[[{"id":"player127","tags":[{"name":"player","properties":{"name":"Vince Carter","age":42}}]}]]}
Example 2: GO FROM "player100", "player102" OVER serve WHERE properties(edge).start_year > 1995 YIELD DISTINCT properties($$).name AS team_name, properties(edge).start_year AS start_year, properties($^).name AS player_name;
Result json (rowOriented,withColumnName) -> [{start_year=1997, player_name=Tim Duncan, team_name=Spurs}, {start_year=2006, player_name=LaMarcus Aldridge, team_name=Trail Blazers}, {start_year=2015, player_name=LaMarcus Aldridge, team_name=Spurs}]
Example 3: GO FROM "player100" OVER follow, serve YIELD properties(edge).degree, properties(edge).start_year;
Result json (rowOriented,withColumnName)-> [{properties(EDGE).start_year=null, properties(EDGE).degree=95}, {properties(EDGE).start_year=null, properties(EDGE).degree=95}, {properties(EDGE).start_year=1997, properties(EDGE).degree=null}]
Example 4: MATCH (v:player{age:32})-[e:follow|:serve]->(v2) RETURN e;
Result json (default)-> [{"dst":"player102","src":"player103","name":"follow","ranking":0,"properties":{"degree":70}},{"dst":"team204","src":"player103","name":"serve","ranking":0,"properties":{"end_year":2019,"start_year":2017}},{"dst":"team208","src":"player103","name":"serve","ranking":0,"properties":{"end_year":2017,"start_year":2013}},{"dst":"team212","src":"player103","name":"serve","ranking":0,"properties":{"end_year":2013,"start_year":2006}},{"dst":"team218","src":"player103","name":"serve","ranking":0,"properties":{"end_year":2013,"start_year":2013}}]
```
### 自定义序列化格式
继承BaseParser
```java
public class CustomVertexParser extends BaseParser {
    public CustomVertexParser(ValueWrapper valueWrapper) {
        super(valueWrapper);
    }

    @Override
    public Object parse() throws UnsupportedEncodingException {
        return "vertex";
    }
}
```
注册class
```
ParserFactory.registerParser(ValueWrapperType.VERTEX,CustomVertexParser.class);
```
#### 在spring中使用
```java
public class CustomVertexParser extends BaseParser implements InitializingBean{
    public CustomVertexParser(ValueWrapper valueWrapper) {
        super(valueWrapper);
    }

    @Override
    public Object parse() throws UnsupportedEncodingException {
        return "vertex";
    }
    
    @Override
    public void afterPropertiesSet() throws Exception{
        ParserFactory.registerParser(ValueWrapperType.VERTEX,CustomVertexParser.class);
    }
}
```