package cn.dengx.cousyncdb.Exceptions;

import java.util.NoSuchElementException;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/12,20:43.
 */
public class NoPrimaryKeyException extends NoSuchElementException {
    public NoPrimaryKeyException() {
    }

    public NoPrimaryKeyException(String detailMessage) {
        super(detailMessage);
    }
}
