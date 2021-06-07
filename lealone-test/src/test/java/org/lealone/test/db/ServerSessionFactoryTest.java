/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.test.db;

import org.junit.Test;
import org.lealone.common.exceptions.DbException;
import org.lealone.db.ConnectionInfo;
import org.lealone.db.api.ErrorCode;
import org.lealone.db.session.ServerSessionFactory;
import org.lealone.test.UnitTestBase;

public class ServerSessionFactoryTest extends UnitTestBase {
    @Test
    public void run() {
        setInMemory(true);
        // setEmbedded(true);

        ConnectionInfo ci;
        try {
            ci = new ConnectionInfo(getURL("NOT_FOUND"));
            ServerSessionFactory.getInstance().createSession(ci).get();
            fail();
        } catch (DbException e) {
            assertEquals(ErrorCode.DATABASE_NOT_FOUND_1, e.getErrorCode());
        }
    }
}
