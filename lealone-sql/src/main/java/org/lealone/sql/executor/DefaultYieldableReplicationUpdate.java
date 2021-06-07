/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.sql.executor;

import org.lealone.db.async.AsyncHandler;
import org.lealone.db.async.AsyncResult;
import org.lealone.db.session.SessionStatus;
import org.lealone.sql.StatementBase;
import org.lealone.storage.replication.ReplicationConflictType;

public class DefaultYieldableReplicationUpdate extends YieldableUpdateBase {

    public DefaultYieldableReplicationUpdate(StatementBase statement, AsyncHandler<AsyncResult<Integer>> asyncHandler) {
        super(statement, asyncHandler);
    }

    @Override
    protected void executeInternal() {
        switch (session.getStatus()) {
        case TRANSACTION_NOT_START:
        case TRANSACTION_NOT_COMMIT:
        case STATEMENT_COMPLETED:
        case RETRYING_RETURN_RESULT: // 重试返回结果后还不能立刻结束，所以也需要转到STATEMENT_RUNNING状态
            session.setStatus(SessionStatus.STATEMENT_RUNNING);
        case RETRYING: // 重试不用返回结果，立即结束
            executeUpdate();
            break;
        }
    }

    private void executeUpdate() {
        int updateCount = statement.update();
        setResult(updateCount);

        // 返回的值为负数时，表示当前语句无法正常执行，需要等待其他事务释放锁
        if (updateCount < 0) {
            session.setStatus(SessionStatus.WAITING);
            session.setReplicationConflictType(ReplicationConflictType.DB_OBJECT_LOCK);
            // 在复制模式下执行时，可以把结果返回给客户端做冲突检测
            if (asyncHandler != null && session.needsHandleReplicationConflict()) {
                asyncHandler.handle(asyncResult);
            }
        } else {
            // 发生复制冲突时当前session进行重试，此时已经不需要再向客户端返回结果了，直接提交即可
            if (session.getStatus() == SessionStatus.RETRYING) {
                stop();
            } else {
                // 此时语句还没有完成，需要等到执行session.handleReplicaConflict后才完成
                if (asyncHandler != null) {
                    AsyncResult<Integer> ar = asyncResult;
                    asyncResult = null; // 避免发送第二次
                    asyncHandler.handle(ar);
                }
            }
        }
    }
}
