package app.kumasuke.test.excel;

import app.kumasuke.excel.HSSFWorkbookEventReader;
import app.kumasuke.excel.WorkbookEventReader;
import app.kumasuke.excel.WorkbookIOException;
import app.kumasuke.excel.XSSFWorkbookEventReader;
import app.kumasuke.util.ResourceUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkbookReaderTest {
    @SuppressWarnings("EmptyTryBlock")
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

        final var plainPath = ResourceUtil.getPathOfClasspathResource("workbook");
        try (final var reader = WorkbookEventReader.open(plainPath)) {
            assertTrue(reader instanceof XSSFWorkbookEventReader);
        }

        final var notWorkbookPath = ResourceUtil.getPathOfClasspathResource("sample-output.xml");
        assertThrows(WorkbookIOException.class, () -> {
            try (final var ignore = WorkbookEventReader.open(notWorkbookPath)) {
                // no-op
            }
        });
    }
}
