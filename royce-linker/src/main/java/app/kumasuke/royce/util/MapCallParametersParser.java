package app.kumasuke.royce.util;

import java.util.Arrays;
import java.util.Map;

public class MapCallParametersParser implements ParametersParser<CallParameter> {
    private final MapParametersParser parser;

    public MapCallParametersParser(String sqlWithNames, Map<String, CallParameter> namedParameters) {
        this.parser = new MapParametersParser(sqlWithNames, namedParameters);
    }

    @Override
    public void parse() {
        parser.parse();
    }

    @Override
    public String getRawSql() {
        return parser.getRawSql();
    }

    @Override
    public CallParameter[] getParameters() {
        return Arrays.stream(parser.getParameters())
                .map(t -> (CallParameter) t)
                .toArray(CallParameter[]::new);
    }
}
