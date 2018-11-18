package app.kumasuke.test.excel;

import app.kumasuke.excel.HSSFWorkbookEventReader;
import org.junit.jupiter.api.Test;

class HSSFWorkbookEventReaderTest extends AbstractWorkbookEventReaderTest {
    private static final String TEST_FILE_NAME = "workbook.xls";

    HSSFWorkbookEventReaderTest() {
        super(TEST_FILE_NAME, HSSFWorkbookEventReader::new);
    }

    @Test
    @Override
    void read() {
        super.read();
    }

    @Test
    @Override
    void cancel() {
        super.cancel();
    }

    @Test
    @Override
    void close() {
        super.close();
    }
}
