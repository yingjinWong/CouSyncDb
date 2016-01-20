package cn.dengx.cousyncdb.Exceptions;

import java.util.NoSuchElementException;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/20,11:36.
 */
public class NoGetMethodException extends NoSuchElementException {
    public NoGetMethodException() {
    }

    public NoGetMethodException(String detailMessage) {
        super(detailMessage);
    }
}
