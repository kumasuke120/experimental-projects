package app.kumasuke.srs.util;

import java.nio.charset.Charset;
import java.util.Arrays;

public class DynamicByteBuffer {
    private static final int BLOCK_BYTE_COUNT = 2048;
    private static final int INITIAL_BLOCK_COUNT = 2;

    private int beginCursor;
    private int endCursor;
    private Object[] blocks;

    public DynamicByteBuffer() {
        this(INITIAL_BLOCK_COUNT * BLOCK_BYTE_COUNT);
    }

    public DynamicByteBuffer(byte[] bytes) {
        this(bytes.length);
        append(bytes);
    }

    private DynamicByteBuffer(int initialCapacity) {
        final int blockCount = initialCapacity / BLOCK_BYTE_COUNT + 1;
        this.blocks = new Object[blockCount > INITIAL_BLOCK_COUNT ? blockCount : INITIAL_BLOCK_COUNT];
        this.beginCursor = 0;
        this.endCursor = 0;
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public int length() {
        return endCursor - beginCursor;
    }

    public byte get(int index) {
        final int actualIndex = index + beginCursor;
        if (actualIndex < beginCursor || actualIndex >= endCursor) {
            throw new IndexOutOfBoundsException();
        } else {
            final var theBlock = (byte[]) blocks[calculateBlockIndex(actualIndex)];
            final int indexOfBlock = calculateIndexAtBlock(actualIndex);
            return theBlock[indexOfBlock];
        }
    }

    public byte[] get(int index, int length) {
        final int actualBeginIndex = index + beginCursor;
        final int actualEndIndex = actualBeginIndex + length;

        if (actualBeginIndex < beginCursor || actualBeginIndex >= endCursor ||
                actualEndIndex - 1 >= endCursor) {
            throw new IndexOutOfBoundsException();
        } else if (actualEndIndex <= actualBeginIndex) {
            throw new IllegalArgumentException();
        } else {
            final byte[] result = new byte[length];

            int remainingBytes = length;
            while (remainingBytes > 0) {
                final int srcActualBeginPos = actualBeginIndex + (length - remainingBytes);
                final var srcBlock = (byte[]) blocks[calculateBlockIndex(srcActualBeginPos)];
                final int srcBeginPos = calculateIndexAtBlock(srcActualBeginPos);
                final int currentBlockValidLength = BLOCK_BYTE_COUNT - srcBeginPos;
                final int dstBeginPos = length - remainingBytes;
                final int copyLength = Math.min(remainingBytes, currentBlockValidLength);

                System.arraycopy(srcBlock, srcBeginPos, result, dstBeginPos, copyLength);

                remainingBytes -= copyLength;
            }

            return result;
        }
    }

    public byte[] pop(int length) {
        if (length <= 0 || length() < length) {
            throw new IllegalArgumentException();
        }

        byte[] result = new byte[length];

        int remainingBytes = length;
        while (remainingBytes > 0) {
            final var srcBlock = (byte[]) blocks[currentBeginBlockIndex()];
            final int srcBeginPos = calculateIndexAtBlock(beginCursor);
            final int currentBlockValidLength = BLOCK_BYTE_COUNT - srcBeginPos;
            final int dstBeginPos = length - remainingBytes;
            final int copyLength = Math.min(remainingBytes, currentBlockValidLength);

            System.arraycopy(srcBlock, srcBeginPos, result, dstBeginPos, copyLength);
            remainingBytes -= copyLength;
            beginCursor += copyLength;

            shrinkBlockWhenPossible();
        }

        return result;
    }

    public void append(byte[] bytes) {
        int remainingBytes = bytes.length;
        while (remainingBytes > 0) {
            final int currentBlockRemainingLength = BLOCK_BYTE_COUNT - currentEndBlockUsedLength();
            final int srcBeginPos = bytes.length - remainingBytes;
            final int dstBeginPos = currentEndBlockUsedLength();
            final int copyLength = Math.min(remainingBytes, currentBlockRemainingLength);

            ensureBlockAvailable();
            final var currentBlock = (byte[]) blocks[currentEndBlockIndex()];
            System.arraycopy(bytes, srcBeginPos, currentBlock, dstBeginPos, copyLength);

            remainingBytes -= copyLength;
            endCursor += copyLength;
        }
    }

    public byte[] toByteArray() {
        final byte[] result = new byte[length()];

        int dstBeginPos = 0;
        for (int i = currentBeginBlockIndex(); i < currentValidBlockCount() + currentBeginBlockIndex(); i++) {
            final var srcBlock = (byte[]) blocks[i];
            final int srcBeginPos = i == currentBeginBlockIndex() ? beginCursor : 0;
            final int copyLength;

            if (i == currentBeginBlockIndex() && i == calculateBlockIndex(endCursor - 1)) {
                // both begin and the last character before the end cursor
                // are on the same block
                copyLength = calculateIndexAtBlock(endCursor - 1) - calculateIndexAtBlock(beginCursor) + 1;
            } else if (i == currentBeginBlockIndex()) { // first block
                copyLength = BLOCK_BYTE_COUNT - calculateIndexAtBlock(beginCursor);
            } else if (i == calculateBlockIndex(endCursor - 1)) { // last block
                copyLength = calculateIndexAtBlock(endCursor - 1) + 1;
            } else {
                copyLength = BLOCK_BYTE_COUNT;
            }

            System.arraycopy(srcBlock, srcBeginPos, result, dstBeginPos, copyLength);
            dstBeginPos += copyLength;
        }

        return result;
    }

    public String toString(Charset charset) {
        return new String(toByteArray(), charset);
    }

    @Override
    public String toString() {
        return toString(Charset.defaultCharset());
    }

    private void shrinkBlockWhenPossible() {
        if (currentBeginBlockIndex() > 0) {
            final int newBlockCount = blocks.length / 2;
            if (newBlockCount >= currentValidBlockCount() && newBlockCount >= INITIAL_BLOCK_COUNT) {
                final var newBlocks = new Object[newBlockCount];
                System.arraycopy(blocks, currentBeginBlockIndex(), newBlocks, 0, currentValidBlockCount());
                blocks = newBlocks;
            } else {
                final int moveCount = currentBeginBlockIndex();
                int i;
                for (i = currentBeginBlockIndex(); i <= currentEndBlockIndex() && i < blocks.length; i++) {
                    blocks[i - moveCount] = blocks[i];
                }
                i -= 1; // moves cursor back to the first useless old block
                for (; i < blocks.length; i++) { // clears old block references to prevent misusing
                    blocks[i] = null;
                }
            }

            final int originalLength = length();
            beginCursor = calculateIndexAtBlock(beginCursor);
            endCursor = beginCursor + originalLength;
        }
    }

    private void ensureBlockAvailable() {
        if (currentEndBlockIndex() >= blocks.length) {
            blocks = Arrays.copyOf(blocks, 2 * blocks.length);
        }

        if (blocks[currentEndBlockIndex()] == null) {
            blocks[currentEndBlockIndex()] = new byte[BLOCK_BYTE_COUNT];
        }
    }

    private int calculateBlockIndex(int index) {
        return index / BLOCK_BYTE_COUNT;
    }

    private int calculateIndexAtBlock(int index) {
        return index % BLOCK_BYTE_COUNT;
    }

    private int currentBeginBlockIndex() {
        return calculateBlockIndex(beginCursor);
    }

    private int currentEndBlockIndex() {
        return calculateBlockIndex(endCursor);
    }

    private int currentEndBlockUsedLength() {
        return calculateIndexAtBlock(endCursor);
    }

    private int currentValidBlockCount() {
        return currentEndBlockIndex() - currentBeginBlockIndex() + (currentEndBlockUsedLength() == 0 ? 0 : 1);
    }
}
