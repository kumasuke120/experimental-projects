package app.kumasuke.excel;

/**
 * An exception denotes the original value in a {@link CellValue} cannot be cast to desired type
 */
public class CellValueCastException extends RuntimeException {
    CellValueCastException() {
    }

    CellValueCastException(Throwable cause) {
        super(cause);
    }
}
