package app.kumasuke.test.excel;

import app.kumasuke.excel.CellValue;
import app.kumasuke.excel.IllegalReaderStateException;
import app.kumasuke.excel.WorkbookEventReader;
import app.kumasuke.excel.XSSFWorkbookEventReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XSSFWorkbookEventReaderTest extends AbstractWorkbookEventReaderTest {
    private static final String TEST_FILE_NAME = "workbook.xlsx";

    XSSFWorkbookEventReaderTest() {
        super(TEST_FILE_NAME, XSSFWorkbookEventReader::new);
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

    @Test
    void setUse1904Windowing() {
        dealWithReader(reader -> {
            assert reader instanceof XSSFWorkbookEventReader;
            ((XSSFWorkbookEventReader) reader).setUse1904Windowing(true);

            reader.read(new WorkbookEventReader.EventHandler() {
                @Override
                public void onStartDocument() {
                    assertThrows(IllegalReaderStateException.class,
                                 () -> ((XSSFWorkbookEventReader) reader).setUse1904Windowing(false));
                }

                @Override
                public void onHandleCell(int sheetIndex, int rowNum, int columnNum, CellValue cellValue) {
                    if (sheetIndex == 0 && (rowNum == 3 || rowNum == 4) && columnNum == 1) {
                        if (!cellValue.isNull()) {
                            assertEquals(2022, cellValue.localDateValue().getYear());
                        }
                    }
                }
            });

            reader.close();

            assertThrows(IllegalReaderStateException.class,
                         () -> ((XSSFWorkbookEventReader) reader).setUse1904Windowing(false));
        });
    }
}
