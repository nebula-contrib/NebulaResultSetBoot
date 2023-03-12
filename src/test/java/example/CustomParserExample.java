package example;

import com.github.dragonchu.rsboot.ResultSetBoot;
import com.github.dragonchu.rsboot.parser.ParserFactory;
import com.github.dragonchu.rsboot.parser.ValueWrapperType;
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

public class CustomParserExample {
    public static void main(String[] args) {
        ParserFactory.registerParser(ValueWrapperType.VERTEX,CustomVertexParser.class);
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
            String example1 = "match (v:player) return v limit 1;";
            System.out.println("Example 1: " + example1);
            resultSet = sessionPool.execute(example1);
            System.out.println("Result json -> " + ResultSetBoot.wrap(resultSet).withColumnName().toJson());
        } catch (IOErrorException | ClientServerIncompatibleException | AuthFailedException | BindSpaceFailedException |
                 UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            sessionPool.close();
        }
    }
}
