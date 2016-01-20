package cn.dengx.cousyncdb.Exceptions;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/14,22:52.
 */
public class CouSyncDbException extends RuntimeException {
    public CouSyncDbException() {
    }

    public CouSyncDbException(String detailMessage) {
        super(detailMessage);
    }
}
