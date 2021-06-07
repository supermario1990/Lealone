/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.transaction;

public abstract class TransactionEngineBase implements TransactionEngine {

    protected final String name;

    public TransactionEngineBase(String name) {
        this.name = name;
        // 见PluggableEngineManager.PluggableEngineService中的注释
        TransactionEngineManager.getInstance().registerEngine(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean supportsMVCC() {
        return false;
    }

    @Override
    public void checkpoint() {
    }
}
