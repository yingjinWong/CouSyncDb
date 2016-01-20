package cn.dengx.cousyncdb.Exceptions;

import java.util.NoSuchElementException;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/20,11:35.
 */
public class NoSetMethodException extends NoSuchElementException {
    public NoSetMethodException() {
    }

    public NoSetMethodException(String detailMessage) {
        super(detailMessage);
    }
}
