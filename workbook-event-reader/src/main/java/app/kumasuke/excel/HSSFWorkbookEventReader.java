package app.kumasuke.excel;

import org.apache.poi.hssf.eventusermodel.AbortableHSSFListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.*;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link WorkbookEventReader} reads a legacy workbook (Excel 97 - 2003) whose file extension
 * usually is <code>xlsx</code>
 */
public class HSSFWorkbookEventReader extends AbstractWorkbookEventReader {
    private InputStream fileIs;
    private POIFSFileSystem poifsFileSystem;

    /**
     * Creates a new {@link HSSFWorkbookEventReader} based on the given file path.
     *
     * @param filePath the file path of the workbook
     */
    public HSSFWorkbookEventReader(Path filePath) {
        super(filePath);
    }

    @Override
    void doOpen(Path filePath) throws Exception {
        fileIs = Files.newInputStream(filePath);
        poifsFileSystem = new POIFSFileSystem(fileIs);
    }

    @Override
    void doRead(EventHandler handler) throws Exception {
        handler.onStartDocument();

        final var request = new HSSFRequest();
        final var readerListener = new ReaderHSSFListener(handler);
        request.addListenerForAllRecords(readerListener);
        final var factory = new HSSFEventFactory();

        try (final DocumentInputStream documentIs =
                     poifsFileSystem.createDocumentInputStream("Workbook")) {
            factory.processEvents(request, documentIs);
        }

        if (isReading()) { // only triggers the event when the reading process wasn't cancelled
            handler.onEndDocument();
        }
    }

    @Override
    void doClose() throws IOException {
        if (poifsFileSystem != null) {
            poifsFileSystem.close();
        }
        if (fileIs != null) {
            fileIs.close();
        }
    }

    private class ReaderHSSFListener extends AbortableHSSFListener {
        private final EventHandler handler;
        private final FormatTrackingHSSFListener formatTracker;

        private boolean use1904Windowing = false;
        private SSTRecord sharedStringTable;
        private Map<Integer, BoundSheetRecord> boundSheets;
        private Map<Integer, RowRecord> currentSheetRows;

        private int previousSheetIndex = -1;
        private int currentSheetIndex = -1;
        private int previousRowNumber = -1;
        private int currentRowNumber = -1;
        private int currentRowEndColumnNum = -1;

        private boolean previousSheetEndHandled = true;
        private boolean previousRowEndHandled = true;

        private Record previousRecord;

        private int tSheetIndex = -1;
        private int tRowNum = -1;

        private ReaderHSSFListener(EventHandler handler) {
            this.handler = handler;
            this.formatTracker = new FormatTrackingHSSFListener(null);
        }

        @Override
        public short abortableProcessRecord(Record record) {
            // records formats and styles
            formatTracker.processRecordInternally(record);

            final short currentSid = record.getSid();
            switch (currentSid) {
                case BoundSheetRecord.sid: {
                    final var boundSheet = (BoundSheetRecord) record;
                    assert boundSheets != null;

                    boundSheets.put(++tSheetIndex, boundSheet);
                    break;
                }
                case EOFRecord.sid: {
                    // this record exists after header or sheet end
                    if (currentSheetIndex != -1) { // not header end
                        handleEndSheet(currentSheetIndex);
                    }

                    break;
                }
                case DateWindow1904Record.sid: {
                    final var dateWindow1904 = (DateWindow1904Record) record;
                    use1904Windowing = dateWindow1904.getWindowing() == 1;
                    break;
                }
                case SSTRecord.sid: {
                    sharedStringTable = (SSTRecord) record;
                    break;
                }
                case BOFRecord.sid: {
                    final var bof = (BOFRecord) record;
                    if (BOFRecord.TYPE_WORKBOOK == bof.getType()) { // workbook starts
                        boundSheets = new HashMap<>();
                    } else if (BOFRecord.TYPE_WORKSHEET == bof.getType()) { // new sheet starts
                        handleStartSheet();
                    }
                    break;
                }
                case RowRecord.sid: {
                    final var row = (RowRecord) record;
                    assert currentSheetRows != null;

                    currentSheetRows.put(++tRowNum, row);
                    break;
                }
                case BlankRecord.sid: {
                    final var blank = (BlankRecord) record;

                    handleCell(blank.getRow(), blank.getColumn(), null);
                    break;
                }
                case MulBlankRecord.sid: {
                    final var mulBlank = (MulBlankRecord) record;

                    for (int column = mulBlank.getFirstColumn();
                         column <= mulBlank.getLastColumn();
                         column++) {
                        handleCell(mulBlank.getRow(), column, null);
                    }
                    break;
                }
                case NumberRecord.sid: {
                    final var number = (NumberRecord) record;

                    final Object cellValue = formatNumberDateCell(number);
                    handleCell(number.getRow(), number.getColumn(), cellValue);
                    break;
                }
                case FormulaRecord.sid: {
                    final var formula = (FormulaRecord) record;

                    @SuppressWarnings("deprecation") final CellType resultType = CellType.forInt(formula.getCachedResultType());
                    switch (resultType) {
                        case NUMERIC: {
                            final Object cellValue = formatNumberDateCell(formula);
                            handleCell(formula.getRow(), formula.getColumn(), cellValue);
                            break;
                        }
                        case STRING: {
                            assert formula.hasCachedResultString();
                            // does nothing, value will be stored in the next StringRecord
                            break;
                        }
                        case BOOLEAN: {
                            final boolean cellValue = formula.getCachedBooleanValue();
                            handleCell(formula.getRow(), formula.getColumn(), cellValue);
                            break;
                        }
                        case ERROR: {
                            handleCell(formula.getRow(), formula.getColumn(), null);
                            break;
                        }
                        default: {
                            throw new AssertionError("Shouldn't happen");
                        }
                    }
                    break;
                }
                case StringRecord.sid: {
                    final var string = (StringRecord) record;
                    if (previousRecord instanceof FormulaRecord) {
                        final var formula = (FormulaRecord) previousRecord;

                        final String cellValue = string.getString();
                        handleCell(formula.getRow(), formula.getColumn(), cellValue);
                    }
                    break;
                }
                case RKRecord.sid: {
                    final var rk = (RKRecord) record;

                    final double cellValue = rk.getRKNumber();
                    handleCell(rk.getRow(), rk.getColumn(), cellValue);
                    break;
                }
                case LabelRecord.sid: {
                    final var label = (LabelRecord) record;

                    final String cellValue = label.getValue();
                    handleCell(label.getRow(), label.getColumn(), cellValue);
                    break;
                }
                case LabelSSTRecord.sid: {
                    final var labelSst = (LabelSSTRecord) record;

                    final int sstIndex = labelSst.getSSTIndex();
                    final String cellValue = sharedStringTable.getString(sstIndex)
                            .getString();
                    handleCell(labelSst.getRow(), labelSst.getColumn(), cellValue);
                    break;
                }
            }

            previousRecord = record;

            // non-zero return value to stop process
            return isReading() ? 0 : Short.MIN_VALUE;
        }

        private void handleStartSheet() {
            if (!previousSheetEndHandled) {
                handleEndSheet(previousSheetIndex);
            }

            previousSheetIndex = currentSheetIndex;
            currentSheetIndex += 1;
            final BoundSheetRecord boundSheet = boundSheets.get(currentSheetIndex);
            assert boundSheet != null;

            final String sheetName = boundSheet.getSheetname();
            handler.onStartSheet(currentSheetIndex, sheetName);

            currentSheetRows = new HashMap<>();
            tRowNum = -1;
            previousRowNumber = -1;
            previousSheetEndHandled = false;
        }

        private Object formatNumberDateCell(CellValueRecordInterface cellRecord) {
            final double value;
            if (cellRecord instanceof NumberRecord) {
                value = ((NumberRecord) cellRecord).getValue();
            } else if (cellRecord instanceof FormulaRecord) {
                value = ((FormulaRecord) cellRecord).getValue();
            } else {
                throw new AssertionError("Shouldn't happen");
            }

            final int formatIndex = formatTracker.getFormatIndex(cellRecord);
            final String formatString = formatTracker.getFormatString(cellRecord);

            if (formatString != null) {
                if (Util.isATextFormat(formatIndex, formatString)) { // deals with cell marked as text
                    if (Util.isAWholeNumber(value)) {
                        return Long.toString((long) (value));
                    } else {
                        return Double.toString(value);
                    }
                } else if (DateUtil.isADateFormat(formatIndex, formatString)) { // deals with date
                    return Util.toJsr310DateOrTime(value, use1904Windowing);
                }
            }

            return value;
        }

        private void handleEndSheet(int sheetIndex) {
            if (!previousRowEndHandled) {
                handleEndRow(currentRowNumber);
            }

            handler.onEndSheet(sheetIndex);
            previousSheetEndHandled = true;
        }

        private void handleCell(int rowNum, int columnNum, Object cellValue) {
            previousRowNumber = currentRowNumber;
            currentRowNumber = rowNum;

            if (previousRowNumber != currentRowNumber) {
                handleStartRow();
            }

            cellValue = Util.toRelativeType(cellValue);
            handler.onHandleCell(currentSheetIndex, rowNum, columnNum, new CellValue(cellValue));

            if (currentRowEndColumnNum == columnNum) {
                handleEndRow(currentRowNumber);
            }
        }

        private void handleStartRow() {
            if (!previousRowEndHandled) {
                handleEndRow(previousRowNumber);
            }

            RowRecord row = currentSheetRows.get(currentRowNumber);
            currentRowEndColumnNum = row.getLastCol() - 1;

            handler.onStartRow(currentSheetIndex, currentRowNumber);
            previousRowEndHandled = false;
        }

        private void handleEndRow(int rowNum) {
            handler.onEndRow(currentSheetIndex, rowNum);
            previousRowEndHandled = true;
        }
    }
}
