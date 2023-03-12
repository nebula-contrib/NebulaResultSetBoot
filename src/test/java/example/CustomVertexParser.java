package example;

import com.github.dragonchu.rsboot.parser.BaseParser;
import com.vesoft.nebula.client.graph.data.ValueWrapper;

import java.io.UnsupportedEncodingException;

public class CustomVertexParser extends BaseParser {
    public CustomVertexParser(ValueWrapper valueWrapper) {
        super(valueWrapper);
    }

    @Override
    public Object parse() throws UnsupportedEncodingException {
        return "vertex";
    }
}
