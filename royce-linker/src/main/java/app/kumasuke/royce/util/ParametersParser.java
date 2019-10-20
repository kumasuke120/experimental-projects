package app.kumasuke.royce.util;

public interface ParametersParser<T> {
    void parse();

    String getRawSql();

    T[] getParameters();
}
