/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.storage.aose;

import org.lealone.db.DataHandler;
import org.lealone.storage.LobStorage;
import org.lealone.storage.Storage;
import org.lealone.storage.StorageBuilder;
import org.lealone.storage.StorageEngineBase;
import org.lealone.storage.aose.lob.LobStreamStorage;

public class AOStorageEngine extends StorageEngineBase {

    public static final String NAME = "AOSE";

    public AOStorageEngine() {
        super(NAME);
    }

    @Override
    public StorageBuilder getStorageBuilder() {
        return new AOStorageBuilder(config, pohFactory);
    }

    @Override
    public LobStorage getLobStorage(DataHandler dataHandler, Storage storage) {
        return new LobStreamStorage(dataHandler, storage);
    }
}
