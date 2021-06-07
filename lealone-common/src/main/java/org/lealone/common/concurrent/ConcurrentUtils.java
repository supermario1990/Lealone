/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.common.concurrent;

public class ConcurrentUtils {

    public static void submitTask(String name, Runnable target) {
        Thread t = new Thread(target, name);
        t.setDaemon(true);
        t.start();
    }

    public static void submitTask(String name, boolean daemon, Runnable target) {
        Thread t = new Thread(target, name);
        t.setDaemon(daemon);
        t.start();
    }

}
