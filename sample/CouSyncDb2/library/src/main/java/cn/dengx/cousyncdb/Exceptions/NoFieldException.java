package cn.dengx.cousyncdb.Exceptions;

import java.util.NoSuchElementException;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/12,22:26.
 */
public class NoFieldException extends NoSuchElementException {
    public NoFieldException() {
    }

    public NoFieldException(String detailMessage) {
        super(detailMessage);
    }
}
