package example;

import com.github.dragonchu.rsboot.ResultSetBoot;
import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class ResultSetBootExample {
    public static void main(String[] args) {
        List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9669));
        String spaceName = "demo_basketballplayer";
        String user = "root";
        String password = "nebula";
        SessionPoolConfig sessionPoolConfig = new SessionPoolConfig(addresses, spaceName, user, password);
        SessionPool sessionPool = new SessionPool(sessionPoolConfig);
        if (!sessionPool.init()) {
            return;
        }
        ResultSet resultSet;
        try {
            String example1 = "match p=(v:player) return p limit 1;";
            System.out.println("Example 1: " + example1);
            resultSet = sessionPool.execute(example1);
            System.out.println("Result json（withColumnName) -> " + ResultSetBoot.wrap(resultSet).withColumnName().toJson());

            String example2 = "GO FROM \"player100\", \"player102\" OVER serve WHERE properties(edge).start_year > 1995 YIELD DISTINCT properties($$).name AS team_name, properties(edge).start_year AS start_year, properties($^).name AS player_name;";
            System.out.println("Example 2: " + example2);
            resultSet = sessionPool.execute(example2);
            System.out.println("Result json (rowOriented,withColumnName) -> " +
                    ResultSetBoot.wrap(resultSet)
                            .rowOriented()
                            .withColumnName()
                            .toJson());

            String example3 = "GO FROM \"player100\" OVER follow, serve YIELD properties(edge).degree, properties(edge).start_year;";
            System.out.println("Example 3: " + example3);
            resultSet = sessionPool.execute(example3);
            System.out.println("Result json (rowOriented,withColumnName)-> " +
                    ResultSetBoot.wrap(resultSet)
                            .rowOriented()
                            .withColumnName()
                            .toJson());

            String example4 = "MATCH (v:player{age:32})-[e:follow|:serve]->(v2) RETURN e;";
            System.out.println("Example 4: " + example4);
            resultSet = sessionPool.execute(example4);
            System.out.println("Result json (default)-> " +
                    ResultSetBoot.wrap(resultSet)
                            .toJson());

            String pathExample = "MATCH p=(v:player{age:32})-[e:follow*1..]->(v2) RETURN p LIMIT 1;";
            System.out.println("Path Example: " + pathExample);
            resultSet = sessionPool.execute(pathExample);
            System.out.println("Result json (default)-> " + ResultSetBoot.wrap(resultSet).toJson());

        } catch (IOErrorException | ClientServerIncompatibleException | AuthFailedException | BindSpaceFailedException |
                 UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            sessionPool.close();
        }
    }

}
