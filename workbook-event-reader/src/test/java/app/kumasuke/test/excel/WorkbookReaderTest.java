package app.kumasuke.test.excel;

import app.kumasuke.excel.HSSFWorkbookEventReader;
import app.kumasuke.excel.WorkbookEventReader;
import app.kumasuke.excel.XSSFWorkbookEventReader;
import app.kumasuke.util.ResourceUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkbookReaderTest {
    @Test
    void open() {
        final var xlsPath = ResourceUtil.getPathOfClasspathResource("workbook.xls");
        try (final var reader = WorkbookEventReader.open(xlsPath)) {
            assertTrue(reader instanceof HSSFWorkbookEventReader);
        }

        final var xlsxPath = ResourceUtil.getPathOfClasspathResource("workbook.xlsx");
        try (final var reader = WorkbookEventReader.open(xlsxPath)) {
            assertTrue(reader instanceof XSSFWorkbookEventReader);
        }
    }
}
