<p align="center">
  <br> English | <a href="README-CN.md">中文</a>
  <br>ResultSetBoot for Nebula ResultSet<br>
</p>

# NebulaResultSetBoot

## Introduction

NebulaResultSetBoot is a small tool to help developers convert Nebula client's query results to JSON.

## Install
clone the source code to local
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
You can also copy the rsboot package from the source code directly into your project
## Usage
### ResultSetBoot
```
ResultSetBoot.wrap(resultSet).toJson();
ResultSetBoot.wrap(resultSet).withColumnName().toJson();
ResultSetBoot.wrap(resultSet).rowOriented().withColumnName().toJson();
```
toJson(): convert to Json object, use fastjson for serialization, and integrate the whole result set by default

withColumnName(): display column name in result set, add this option to aggregate by column by default

rowOriented(): aggregated by row

matrixStyle(): aggregation by column

```
Example 1: match p=(v:player) return p limit 1;
Result json（withColumnName) -> {"p":[[{"id":"player127","tags":[{"name":"player","properties":{"name":"Vince Carter","age":42}}]}]]}
```
```
Example 2: GO FROM "player100", "player102" OVER serve WHERE properties(edge).start_year > 1995 YIELD DISTINCT properties($$).name AS team_name, properties(edge).start_year AS start_year, properties($^).name AS player_name;
Result json (rowOriented,withColumnName) -> [{start_year=1997, player_name=Tim Duncan, team_name=Spurs}, {start_year=2006, player_name=LaMarcus Aldridge, team_name=Trail Blazers}, {start_year=2015, player_name=LaMarcus Aldridge, team_name=Spurs}]
```
```
Example 3: GO FROM "player100" OVER follow, serve YIELD properties(edge).degree, properties(edge).start_year;
Result json (rowOriented,withColumnName)-> [{properties(EDGE).start_year=null, properties(EDGE).degree=95}, {properties(EDGE).start_year=null, properties(EDGE).degree=95}, {properties(EDGE).start_year=1997, properties(EDGE).degree=null}]
```
```
Example 4: MATCH (v:player{age:32})-[e:follow|:serve]->(v2) RETURN e;
Result json (default)-> [{"dst":"player102","src":"player103","name":"follow","ranking":0,"properties":{"degree":70}},{"dst":"team204","src":"player103","name":"serve","ranking":0,"properties":{"end_year":2019,"start_year":2017}},{"dst":"team208","src":"player103","name":"serve","ranking":0,"properties":{"end_year":2017,"start_year":2013}},{"dst":"team212","src":"player103","name":"serve","ranking":0,"properties":{"end_year":2013,"start_year":2006}},{"dst":"team218","src":"player103","name":"serve","ranking":0,"properties":{"end_year":2013,"start_year":2013}}]
```
### Custom Serialization Format
Inherit BaseParser
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
Register class
```
ParserFactory.registerParser(ValueWrapperType.VERTEX,CustomVertexParser.class);
```
#### Use in spring framework
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