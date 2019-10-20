package app.kumasuke.royce.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapParametersParser implements ParametersParser<Object> {
    private static final int PS_READY = 0;
    private static final int PS_ORDERED_NAME_SET = 1;
    private static final int PS_RAW_SQL_SET = 2;
    private static final int PS_PARAMETERS_SET = 4;

    private static final Pattern sqlNamePattern = Pattern.compile(":([_a-zA-Z]\\w*)");

    private final String sqlWithNames;

    private Map<String, ?> namedParameters;
    private Matcher sqlMatcher;
    private List<String> orderedNames;
    private String rawSql;
    private Object[] parameters;
    private int parseState;


    public MapParametersParser(String sqlWithNames, Map<String, ?> namedParameters) {
        this.sqlWithNames = sqlWithNames;
        this.namedParameters = namedParameters;
        this.parseState = PS_READY;
    }

    @Override
    public void parse() {
        parseAndSetOrderedNames();
        parseAndSetRawSql();
        parseAndSetParameters();
    }

    public void setNamedParameters(Map<String, ?> namedParameters) {
        this.namedParameters = namedParameters;
        parseState &= ~PS_PARAMETERS_SET;
    }

    @Override
    public String getRawSql() {
        if (isParsed()) {
            return rawSql;
        } else {
            throw new IllegalStateException("named parameters haven't been parsed yet");
        }
    }

    @Override
    public Object[] getParameters() {
        if (isParsed()) {
            return parameters.clone();
        } else {
            throw new IllegalStateException("named parameters haven't been parsed yet");
        }
    }

    private void parseAndSetOrderedNames() {
        if ((parseState & PS_ORDERED_NAME_SET) != PS_ORDERED_NAME_SET) {
            final List<String> tmp = new LinkedList<>();
            sqlMatcher = sqlNamePattern.matcher(sqlWithNames);
            while (sqlMatcher.find()) {
                String name = sqlMatcher.group(1);
                tmp.add(name);
            }
            orderedNames = Collections.unmodifiableList(tmp);
            parseState |= PS_ORDERED_NAME_SET;
        }
    }

    private void parseAndSetRawSql() {
        if ((parseState & PS_RAW_SQL_SET) != PS_RAW_SQL_SET) {
            rawSql = sqlMatcher.replaceAll("?");
            parseState |= PS_RAW_SQL_SET;
        }
    }

    private void parseAndSetParameters() {
        if ((parseState & PS_PARAMETERS_SET) != PS_PARAMETERS_SET) {
            final Map<String, ?> parametersMap = namedParameters == null ?
                    Collections.emptyMap() : namedParameters;
            parameters = orderedNames.stream()
                    .map(parametersMap::get)
                    .toArray(Object[]::new);
            parseState |= PS_PARAMETERS_SET;
        }
    }

    private boolean isParsed() {
        return parseState == (PS_ORDERED_NAME_SET | PS_RAW_SQL_SET | PS_PARAMETERS_SET);
    }
}
