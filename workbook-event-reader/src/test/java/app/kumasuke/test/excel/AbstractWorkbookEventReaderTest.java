package app.kumasuke.test.excel;

import app.kumasuke.excel.CellValue;
import app.kumasuke.excel.IllegalReaderStateException;
import app.kumasuke.excel.WorkbookEventReader;
import app.kumasuke.util.ResourceUtil;
import app.kumasuke.util.XmlUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractWorkbookEventReaderTest {
    private final String testFileName;
    private final Function<Path, WorkbookEventReader> constructor;

    AbstractWorkbookEventReaderTest(String testFileName,
                                    Function<Path, WorkbookEventReader> constructor) {
        this.testFileName = testFileName;
        this.constructor = constructor;
    }

    final void dealWithReader(Consumer<WorkbookEventReader> consumer) {
        final Path filePath = ResourceUtil.getPathOfClasspathResource(testFileName);
        try (final WorkbookEventReader reader = constructor.apply(filePath)) {
            consumer.accept(reader);
        }
    }

    void read() {
        dealWithReader(reader -> {
            final var handler = new TestEventHandler();
            reader.read(handler);

            final String xml = handler.getXml();
            assertSameWithSample("sample-output.xml", xml);

            // read cannot be started when reader is in reading process
            reader.read(new TestEventHandler() {
                @Override
                public void onStartDocument() {
                    assertThrows(IllegalReaderStateException.class, () -> reader.read(handler));
                }
            });
        });
    }

    void cancel() {
        dealWithReader(reader -> {
            final var handler = new TestEventHandler() {
                @Override
                public void onEndSheet(int sheetIndex) {
                    super.onEndSheet(sheetIndex);

                    if (sheetIndex == 0 && reader != null) {
                        reader.cancel();
                    }
                }
            };

            reader.read(handler);

            handler.onEndDocument();
            final String xml = handler.getXml();
            assertSameWithSample("sample-output-2.xml", xml);

            // reader is not being read
            assertThrows(IllegalReaderStateException.class, reader::cancel);
        });
    }

    void close() {
        dealWithReader(reader -> {
            reader.close();

            assertThrows(IllegalReaderStateException.class, () ->
                    reader.read(new TestEventHandler() {
                        @Override
                        public void onEndSheet(int sheetIndex) {
                            if (sheetIndex == 0) {
                                reader.close();
                            }
                            super.onEndSheet(sheetIndex);
                        }
                    }));

            assertThrows(IllegalReaderStateException.class, () -> reader.read(new TestEventHandler()));

            // at this point, the reader has been closed
            // however, at the end of dealWithReader, the close method will be invoked
            // one more time, that's intended for testing if reader could be closed multiple times
        });
    }

    private void assertSameWithSample(String sampleFileName, String actualXml) {
        final String expectedXml;
        try {
            expectedXml = ResourceUtil.loadClasspathResourceToString(sampleFileName);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertTrue(XmlUtil.isSameXml(expectedXml, actualXml));
    }

    private static class TestEventHandler implements WorkbookEventReader.EventHandler {
        private final Stack<Integer> sheetStack;
        private final Stack<Integer> rowStack;
        private final StringBuilder xml;


        TestEventHandler() {
            xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sheetStack = new Stack<>();
            rowStack = new Stack<>();
        }

        String getXml() {
            return xml.toString();
        }

        @Override
        public void onStartDocument() {
            xml.append("<document>");
        }

        @Override
        public void onEndDocument() {
            xml.append("</document>");
        }

        @Override
        public void onStartSheet(int sheetIndex, String sheetName) {
            xml.append("<sheet index=\"")
                    .append(sheetIndex)
                    .append("\" name=\"")
                    .append(sheetName)
                    .append("\">");

            sheetStack.push(sheetIndex);

            if (sheetIndex == 0) {
                assertEquals("Sheet1", sheetName);
            } else if (sheetIndex == 1) {
                assertEquals("Sheet2", sheetName);
            } else {
                throw new AssertionError();
            }
        }

        @Override
        public void onEndSheet(int sheetIndex) {
            xml.append("</sheet>");

            final int poppedSheetIndex = sheetStack.pop();
            assertEquals(sheetIndex, poppedSheetIndex);
        }

        @Override
        public void onStartRow(int sheetIndex, int rowNum) {
            xml.append("<row index=\"")
                    .append(rowNum)
                    .append("\">");

            final int currentSheetIndex = sheetStack.peek();
            assertEquals(currentSheetIndex, sheetIndex);

            rowStack.push(rowNum);
        }

        @Override
        public void onEndRow(int sheetIndex, int rowNum) {
            xml.append("</row>");

            final int currentSheetIndex = sheetStack.peek();
            assertEquals(currentSheetIndex, sheetIndex);

            final int poppedRowNum = rowStack.pop();
            assertEquals(rowNum, poppedRowNum);
        }

        @Override
        public void onHandleCell(int sheetIndex, int rowNum, int columnNum, CellValue cellValue) {
            xml.append("<cell javaType=\"")
                    .append(cellValue.isNull() ? null : cellValue.originalType().getCanonicalName())
                    .append("\" index=\"")
                    .append(columnNum)
                    .append("\">")
                    .append(cellValue.isNull() ? "" : cellValue.originalValue())
                    .append("</cell>");

            final int currentSheetIndex = sheetStack.peek();
            assertEquals(currentSheetIndex, sheetIndex);

            final int currentRowNum = rowStack.peek();
            assertEquals(rowNum, currentRowNum);
        }
    }
}
