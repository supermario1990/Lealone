/*
 * Copyright 2004-2014 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.lealone.db.result;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.lealone.common.exceptions.DbException;
import org.lealone.common.util.Utils;
import org.lealone.db.Constants;
import org.lealone.db.DataBuffer;
import org.lealone.db.Database;
import org.lealone.db.session.ServerSession;
import org.lealone.db.value.Value;
import org.lealone.storage.fs.FileStorage;

/**
 * This class implements the disk buffer for the LocalResult class.
 */
class ResultDiskBuffer implements ResultExternal {

    private static final int READ_AHEAD = 128;

    private final DataBuffer rowBuff;
    private final ArrayList<ResultDiskTape> tapes;
    private final ResultDiskTape mainTape;
    private final SortOrder sort;
    private final int columnCount;
    private final int maxBufferSize;

    private FileStorage file;
    private int rowCount;

    private final ResultDiskBuffer parent;
    private boolean closed;
    private int childCount;

    /**
     * Represents a virtual disk tape for the merge sort algorithm.
     * Each virtual disk tape is a region of the temp file.
     */
    static class ResultDiskTape {

        /**
         * The start position of this tape in the file.
         */
        long start;

        /**
         * The end position of this tape in the file.
         */
        long end;

        /**
         * The current read position.
         */
        long pos;

        /**
         * A list of rows in the buffer.
         */
        ArrayList<Value[]> buffer = Utils.newSmallArrayList();
    }

    ResultDiskBuffer(ServerSession session, SortOrder sort, int columnCount) {
        this.parent = null;
        this.sort = sort;
        this.columnCount = columnCount;
        Database db = session.getDatabase();
        rowBuff = DataBuffer.create(db, Constants.DEFAULT_PAGE_SIZE);
        String fileName = db.createTempFile();
        file = db.openFile(fileName, "rw", false);
        file.setCheckedWriting(false);
        file.seek(FileStorage.HEADER_LENGTH);
        if (sort != null) {
            tapes = Utils.newSmallArrayList();
            mainTape = null;
        } else {
            tapes = null;
            mainTape = new ResultDiskTape();
            mainTape.pos = FileStorage.HEADER_LENGTH;
        }
        this.maxBufferSize = db.getSettings().largeResultBufferSize;
    }

    private ResultDiskBuffer(ResultDiskBuffer parent) {
        this.parent = parent;
        rowBuff = DataBuffer.create(parent.rowBuff.getHandler(), Constants.DEFAULT_PAGE_SIZE);
        file = parent.file;
        if (parent.tapes != null) {
            tapes = new ArrayList<>(parent.tapes.size());
            for (ResultDiskTape t : parent.tapes) {
                ResultDiskTape t2 = new ResultDiskTape();
                t2.pos = t2.start = t.start;
                t2.end = t.end;
                tapes.add(t2);
            }
        } else {
            tapes = null;
        }
        if (parent.mainTape != null) {
            mainTape = new ResultDiskTape();
            mainTape.pos = FileStorage.HEADER_LENGTH;
            mainTape.start = parent.mainTape.start;
            mainTape.end = parent.mainTape.end;
        } else {
            mainTape = null;
        }
        sort = parent.sort;
        columnCount = parent.columnCount;
        maxBufferSize = parent.maxBufferSize;
    }

    @Override
    public synchronized ResultDiskBuffer createShallowCopy() {
        if (closed || parent != null) {
            return null;
        }
        childCount++;
        return new ResultDiskBuffer(this);
    }

    @Override
    public int addRows(ArrayList<Value[]> rows) {
        if (sort != null) {
            sort.sort(rows);
        }
        DataBuffer buff = rowBuff;
        long start = file.getFilePointer();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bufferLen = 0;
        for (Value[] row : rows) {
            buff.reset();
            buff.putInt(0);
            for (int j = 0; j < columnCount; j++) {
                Value v = row[j];
                buff.writeValue(v);
            }
            buff.fillAligned();
            int len = buff.length();
            buff.putInt(0, len);
            if (maxBufferSize > 0) {
                buffer.write(buff.getBytes(), 0, len);
                bufferLen += len;
                if (bufferLen > maxBufferSize) {
                    byte[] data = buffer.toByteArray();
                    buffer.reset();
                    file.write(data, 0, data.length);
                    bufferLen = 0;
                }
            } else {
                file.write(buff.getBytes(), 0, len);
            }
        }
        if (bufferLen > 0) {
            byte[] data = buffer.toByteArray();
            file.write(data, 0, data.length);
        }
        if (sort != null) {
            ResultDiskTape tape = new ResultDiskTape();
            tape.start = start;
            tape.end = file.getFilePointer();
            tapes.add(tape);
        } else {
            mainTape.end = file.getFilePointer();
        }
        rowCount += rows.size();
        return rowCount;
    }

    @Override
    public void done() {
        file.seek(FileStorage.HEADER_LENGTH);
        file.autoDelete();
    }

    @Override
    public void reset() {
        if (sort != null) {
            for (ResultDiskTape tape : tapes) {
                tape.pos = tape.start;
                tape.buffer = Utils.newSmallArrayList();
            }
        } else {
            mainTape.pos = FileStorage.HEADER_LENGTH;
            mainTape.buffer = Utils.newSmallArrayList();
        }
    }

    private void readRow(ResultDiskTape tape) {
        int min = Constants.FILE_BLOCK_SIZE;
        DataBuffer buff = rowBuff;
        buff.reset();
        file.readFully(buff.getBytes(), 0, min);
        int len = buff.getInt();
        buff.checkCapacity(len);
        if (len - min > 0) {
            file.readFully(buff.getBytes(), min, len - min);
        }
        tape.pos += len;
        Value[] row = new Value[columnCount];
        for (int k = 0; k < columnCount; k++) {
            row[k] = buff.readValue();
        }
        tape.buffer.add(row);
    }

    @Override
    public Value[] next() {
        return sort != null ? nextSorted() : nextUnsorted();
    }

    private Value[] nextUnsorted() {
        file.seek(mainTape.pos);
        if (mainTape.buffer.isEmpty()) {
            for (int j = 0; mainTape.pos < mainTape.end && j < READ_AHEAD; j++) {
                readRow(mainTape);
            }
        }
        Value[] row = mainTape.buffer.get(0);
        mainTape.buffer.remove(0);
        return row;
    }

    private Value[] nextSorted() {
        int next = -1;
        for (int i = 0, size = tapes.size(); i < size; i++) {
            ResultDiskTape tape = tapes.get(i);
            if (tape.buffer.isEmpty() && tape.pos < tape.end) {
                file.seek(tape.pos);
                for (int j = 0; tape.pos < tape.end && j < READ_AHEAD; j++) {
                    readRow(tape);
                }
            }
            if (tape.buffer.size() > 0) {
                if (next == -1) {
                    next = i;
                } else if (compareTapes(tape, tapes.get(next)) < 0) {
                    next = i;
                }
            }
        }
        ResultDiskTape t = tapes.get(next);
        Value[] row = t.buffer.get(0);
        t.buffer.remove(0);
        return row;
    }

    private int compareTapes(ResultDiskTape a, ResultDiskTape b) {
        Value[] va = a.buffer.get(0);
        Value[] vb = b.buffer.get(0);
        return sort.compare(va, vb);
    }

    private synchronized void closeChild() {
        if (--childCount == 0 && closed) {
            file.closeAndDeleteSilently();
            file = null;
        }
    }

    @Override
    protected void finalize() {
        close();
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        if (parent != null) {
            parent.closeChild();
        } else if (file != null) {
            if (childCount == 0) {
                file.closeAndDeleteSilently();
                file = null;
            }
        }
    }

    @Override
    public int removeRow(Value[] values) {
        throw DbException.throwInternalError();
    }

    @Override
    public boolean contains(Value[] values) {
        throw DbException.throwInternalError();
    }

    @Override
    public int addRow(Value[] values) {
        throw DbException.throwInternalError();
    }

}
