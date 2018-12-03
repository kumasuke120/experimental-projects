package app.kumasuke.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

/**
 * A {@link WorkbookEventReader} reads a SpreadsheetML workbook (Excel 2007 onwards) whose file extension
 * usually is <code>xlsx</code>
 */
public class XSSFWorkbookEventReader extends AbstractWorkbookEventReader {
    private volatile boolean use1904Windowing = false;

    private OPCPackage opcPackage;
    private XSSFReader xssfReader;
    private SharedStringsTable sharedStringsTable;
    private StylesTable stylesTable;

    /**
     * Creates a new {@link XSSFWorkbookEventReader} based on the given file path.
     *
     * @param filePath the file path of the workbook
     * @throws NullPointerException <code>filePath</code> is <code>null</code>
     * @throws WorkbookIOException  errors happened when opening
     */
    public XSSFWorkbookEventReader(Path filePath) {
        super(filePath);
    }

    /**
     * Sets this {@link XSSFWorkbookEventReader} to use 1904 windowing to parse date cells.
     *
     * @param use1904Windowing whether use 1904 date windowing or not
     * @throws IllegalReaderStateException this {@link WorkbookEventReader} has been closed;
     *                                     or called during reading process
     */
    public void setUse1904Windowing(boolean use1904Windowing) {
        assertNotClosed();
        assertNotBeingRead();

        this.use1904Windowing = use1904Windowing;
    }

    @Override
    void doOpen(Path filePath) throws Exception {
        final File file = filePath.toFile();

        opcPackage = OPCPackage.open(file);
        xssfReader = new XSSFReader(opcPackage);
        sharedStringsTable = xssfReader.getSharedStringsTable();
        stylesTable = xssfReader.getStylesTable();
    }

    @Override
    ReaderCleanAction createCleanAction() {
        return new XSSFReaderCleanAction(this);
    }

    @Override
    void doRead(EventHandler handler) throws Exception {
        int currentSheetIndex = -1;

        handler.onStartDocument();

        final SAXParser saxParser = createSAXParser();
        final var saxHandler = new ReaderSheetHandler(handler);

        final XSSFReader.SheetIterator sheetIt = getSheetIterator();
        while (sheetIt.hasNext()) {
            if (!isReading()) {
                break;
            }

            try (InputStream sheetIs = sheetIt.next()) {
                String sheetName = sheetIt.getSheetName();
                handler.onStartSheet(++currentSheetIndex, sheetName);

                saxHandler.setCurrentSheetIndex(currentSheetIndex);
                try {
                    saxParser.parse(sheetIs, saxHandler);
                } catch (CancelReadingException e) {
                    // stops parsing and cancels reading
                    break;
                }

                handler.onEndSheet(currentSheetIndex);
            }
        }

        if (isReading()) { // only triggers the event when the reading process wasn't cancelled
            handler.onEndDocument();
        }
    }

    private XSSFReader.SheetIterator getSheetIterator() throws IOException, InvalidFormatException {
        final Iterator<InputStream> sheetsData = xssfReader.getSheetsData();
        assert sheetsData instanceof XSSFReader.SheetIterator;
        return (XSSFReader.SheetIterator) sheetsData;
    }

    private SAXParser createSAXParser() throws ParserConfigurationException, SAXException {
        final var factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newSAXParser();
    }

    // stops EventHandler, not an actual exception
    private static class CancelReadingException extends SAXException {
    }

    private static class XSSFReaderCleanAction extends ReaderCleanAction {
        private final OPCPackage opcPackage;

        XSSFReaderCleanAction(XSSFWorkbookEventReader reader) {
            this.opcPackage = reader.opcPackage;
        }

        @Override
        void doClean() throws Exception {
            if (opcPackage != null) {
                opcPackage.close();
            }
        }
    }

    private class ReaderSheetHandler extends DefaultHandler {
        // tags
        private static final String TAG_ROW = "row";
        private static final String TAG_CELL = "c";
        private static final String TAG_CELL_VALUE = "v";
        private static final String TAG_INLINE_CELL_VALUE = "t";

        // attribute for TAG_ROW
        private static final String ATTRIBUTE_ROW_REFERENCE = "r";

        // attributes for TAG_CELL
        private static final String ATTRIBUTE_CELL_REFERENCE = "r";
        private static final String ATTRIBUTE_CELL_TYPE = "t";
        private static final String ATTRIBUTE_CELL_STYLE = "s";

        // attribute values for ATTRIBUTE_CELL_REFERENCE
        private static final String CELL_TYPE_SHARED_STRING = "s";
        private static final String CELL_TYPE_INLINE_STRING = "inlineStr";
        private static final String CELL_TYPE_BOOLEAN = "b";
        private static final String CELL_TYPE_ERROR = "e";

        // cell values for boolean
        private static final String CELL_VALUE_BOOLEAN_TRUE = "1";
        private static final String CELL_VALUE_BOOLEAN_FALSE = "0";

        private final EventHandler handler;

        private int currentSheetIndex = -1;
        private int currentRowNum = -1;
        private String currentCellReference;
        private int currentCellXfIndex = -1;
        private StringBuilder currentCellValue;

        private boolean isCurrentCellValue = false;

        private boolean isCurrentSharedString = false;
        private boolean isCurrentInlineString = false;
        private boolean isCurrentBoolean = false;
        private boolean isCurrentError = false;

        ReaderSheetHandler(EventHandler handler) {
            this.handler = handler;
        }

        void setCurrentSheetIndex(int currentSheetIndex) {
            this.currentSheetIndex = currentSheetIndex;
        }

        @Override
        public final void startElement(String uri, String localName,
                                       String qName, Attributes attributes) throws SAXException {
            cancelReadingWhenNecessary();

            if (TAG_CELL.equals(localName)) {
                currentCellReference = attributes.getValue(ATTRIBUTE_CELL_REFERENCE);
                currentCellXfIndex = Util.toInt(attributes.getValue(ATTRIBUTE_CELL_STYLE), -1);

                String cellType = attributes.getValue(ATTRIBUTE_CELL_TYPE);
                isCurrentSharedString = CELL_TYPE_SHARED_STRING.equals(cellType);
                isCurrentInlineString = CELL_TYPE_INLINE_STRING.equals(cellType);
                isCurrentBoolean = CELL_TYPE_BOOLEAN.equals(cellType);
                isCurrentError = CELL_TYPE_ERROR.equals(cellType);
            } else if (isCellValueRelated(localName)) {
                isCurrentCellValue = true; // indicates cell value starts
            } else if (TAG_ROW.equals(localName)) {
                try {
                    currentRowNum = Integer.parseInt(attributes.getValue(ATTRIBUTE_ROW_REFERENCE)) - 1;
                } catch (NumberFormatException e) {
                    throw new SAXParseException("Cannot parse row number in tag '" + qName + "'",
                                                null, e);
                }

                handler.onStartRow(currentSheetIndex, currentRowNum);
            }

            currentCellValue = new StringBuilder();
        }

        @Override
        public final void endElement(String uri, String localName, String qName) throws SAXException {
            cancelReadingWhenNecessary();

            if (isCellValueRelated(localName)) {
                isCurrentCellValue = false; // indicates cell value ends
            } else if (TAG_CELL.equals(localName)) {
                Map.Entry<Integer, Integer> rowAndColumn = Util.cellReferenceToRowAndColumn(currentCellReference);

                final int rowNum = rowAndColumn.getKey();
                final int columnNum = rowAndColumn.getValue();

                assert rowNum == currentRowNum;
                if (rowNum != -1 && columnNum != -1) {
                    Object cellValue = getCellValue(localName);
                    handler.onHandleCell(currentSheetIndex, currentRowNum, columnNum, new CellValue(cellValue));
                } else {
                    throw new SAXParseException("Cannot parse row number or column number in tag '" + qName + "'",
                                                null);
                }
            } else if (TAG_ROW.equals(localName)) {
                handler.onEndRow(currentSheetIndex, currentRowNum);
            }
        }

        private boolean isCellValueRelated(String localName) {
            return TAG_CELL_VALUE.equals(localName) ||
                    (isCurrentInlineString && TAG_INLINE_CELL_VALUE.equals(localName));
        }

        private Object getCellValue(String localName) throws SAXParseException {
            String cellValueStr = currentCellValue.toString();

            if (isCurrentError) {
                cellValueStr = null;
            } else if (isCurrentSharedString) {
                final int sharedStringIndex;
                try {
                    sharedStringIndex = Integer.parseInt(cellValueStr);
                } catch (NumberFormatException e) {
                    throw new SAXParseException(
                            "Cannot parse shared string index in tag '" + localName + "', " +
                                    "which should be a int: " + cellValueStr,
                            null, e);
                }

                final RichTextString sharedString = sharedStringsTable.getItemAt(sharedStringIndex);
                cellValueStr = sharedString.getString();

                isCurrentSharedString = false;
            }

            Object cellValue;
            if (isCurrentBoolean) {
                if (CELL_VALUE_BOOLEAN_TRUE.equals(cellValueStr)) {
                    cellValue = true;
                } else if (CELL_VALUE_BOOLEAN_FALSE.equals(cellValueStr)) {
                    cellValue = false;
                } else {
                    throw new SAXParseException("Cannot parse boolean value in tag '" + localName + "', " +
                                                        "which should be 'TRUE' or 'FALSE': " + cellValueStr,
                                                null);
                }
            } else {
                cellValue = formatNumberDateCellValue(cellValueStr);
            }

            return Util.toRelativeType(cellValue);
        }

        private Object formatNumberDateCellValue(String cellValueStr) {
            Object cellValue;

            if (cellValueStr == null || cellValueStr.isEmpty()) {
                cellValue = null;
            } else if (Util.isADecimalFraction(cellValueStr)) {
                // actually, won't return default value
                double doubleValue = Util.toDouble(cellValueStr, Double.NaN);

                cellValue = doubleValue;
                if (currentCellXfIndex != -1) {
                    final short formatIndex = getFormatIndex(currentCellXfIndex);
                    final String formatString = getFormatString(formatIndex);

                    if (Util.isATextFormat(formatIndex, formatString)) { // deals with cell marked as text
                        if (Util.isAWholeNumber(doubleValue)) {
                            cellValue = Long.toString((long) (doubleValue));
                        } else {
                            cellValue = Double.toString(doubleValue);
                        }
                    } else if (formatString != null) { // deals with date
                        if (DateUtil.isADateFormat(formatIndex, formatString)) {
                            cellValue = Util.toJsr310DateOrTime(doubleValue, use1904Windowing);
                        }
                    }
                }
            } else {
                cellValue = cellValueStr;
            }

            return cellValue;
        }

        private short getFormatIndex(int cellXfIndex) {
            CTXf cellXf = stylesTable.getCellXfAt(cellXfIndex);
            if (cellXf.isSetNumFmtId()) {
                return (short) cellXf.getNumFmtId();
            } else {
                // valid numFmtId is non-negative
                return -1;
            }
        }

        private String getFormatString(short numFmtId) {
            String numberFormat = stylesTable.getNumberFormatAt(numFmtId);
            if (numberFormat == null) numberFormat = BuiltinFormats.getBuiltinFormat(numFmtId);
            return numberFormat;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            cancelReadingWhenNecessary();

            if (isCurrentCellValue) { // only records when cell value starts
                currentCellValue.append(ch, start, length);
            }
        }

        private void cancelReadingWhenNecessary() throws SAXException {
            if (!isReading()) {
                throw new CancelReadingException();
            }
        }
    }
}
