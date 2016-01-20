package cn.dengx.cousyncdb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/12,14:06.
 */
public class FieldContainer {
    private Field field;
    private Method methodGet;
    private Method methodSet;
    private boolean isPrimaryKey;
    private boolean isAuto;
    private boolean ignore;

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getMethodGet() {
        return methodGet;
    }

    public void setMethodGet(Method methodGet) {
        this.methodGet = methodGet;
    }

    public Method getMethodSet() {
        return methodSet;
    }

    public void setMethodSet(Method methodSet) {
        this.methodSet = methodSet;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setIsAuto(boolean isAuto) {
        this.isAuto = isAuto;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public FieldContainer() {
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(field.getName());
        builder.append("-").append("isPrimaryKey ").append(isPrimaryKey)
                .append("-").append("isAuto ").append(isAuto).append("-")
                .append("ignore ").append(ignore);
        return builder.toString();
    }
}
