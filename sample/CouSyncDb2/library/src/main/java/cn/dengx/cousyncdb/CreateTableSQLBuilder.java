package cn.dengx.cousyncdb;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.dengx.cousyncdb.Exceptions.NoFieldException;
import cn.dengx.cousyncdb.Exceptions.NoPrimaryKeyException;
import cn.dengx.cousyncdb.annotations.Ignore;
import cn.dengx.cousyncdb.annotations.PrimaryKey;
import cn.dengx.cousyncdb.annotations.Table;
import cn.dengx.cousyncdb.util.LogUtil;
import cn.dengx.cousyncdb.util.SqlUtil;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/12,14:17.
 */
public class CreateTableSQLBuilder {
    private CreateTableSQLBuilder() {
    }

    public static String getTableName(@NonNull Container container) {
        String name;
        Table table = container.getTable();
        if (table != null && !TextUtils.isEmpty(table.Name())) {
            name = table.Name();
        } else {
            name = container.getModelClass().getSimpleName();
        }
        return name;
    }

    public static String getTableName(@NonNull Object o) {
        Container container = ClassParser.getContainer(o.getClass());
        String name = null;
        if (container != null) {
            name = getTableName(container);
        }
        return name;
    }

    /**
     * 获得建表语句
     *
     * @param container
     * @return
     */
    public static String builder(Container container) {
        if (container == null)
            throw new IllegalArgumentException("CreateTableBuilder don't build sql with null");
        StringBuilder builder = new StringBuilder();
        builder.append(getTableSQL(container));//create table if not exists name(
        String primaryKey = getPrimaryKeySQL(container);
        if (!TextUtils.isEmpty(primaryKey))
            builder.append(primaryKey);//column type primary key autoincrement,
        else
            LogUtil.d(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + container.getModelClass() +
                    " discover no primary key when create table sql");
        builder.append(getTypesSQL(container));
        builder.append(Statement.BRACKET_RIGHT);
        builder.append(Statement.COLON);
        return builder.toString();
    }

    static String getTypesSQL(@NonNull Container container) {
        StringBuilder builder = new StringBuilder();
        Field[] fields = container.getFields();
        FieldContainer primary = container.getPrimaryField();
        if (primary == null)
            primary = getPrimaryFieldContainer(container);
        Field primaryField = null;
        if (primary != null)
            primaryField = primary.getField();
        if (fields != null && fields.length > 0) {
            boolean isFirst = true;
            for (Field f : fields) {
                Ignore ignore = f.getAnnotation(Ignore.class);
                if (ignore != null) {
                    LogUtil.d(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "ignore field=" + f.getName() +
                            " when " + container.getModelName() + "create type column sql");
                    continue;
                }
                if (primaryField != null && f.equals(primaryField)) {
                    LogUtil.d(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "jump primary key field=" + f.getName() +
                            " when " + container.getModelName() + "create type column sql");
                    continue;
                }
                String typeSql = getTypeString(f);
                if (TextUtils.isEmpty(typeSql))
                    continue;
                if (isFirst) {
                    isFirst = false;
                } else
                    builder.append(Statement.COMMA);
                builder.append(typeSql);

            }
        } else {
            throw new NoFieldException(container.getModelName() + " have no field");
        }
        return builder.toString();
    }

    static String getTableSQL(@NonNull Container container) {
        StringBuilder builder = new StringBuilder(Statement.CREATE);
        builder.append(Statement.TABLE);
        builder.append(Statement.IF_NOT_EXISTS);
        builder.append(Statement.SPACE);
        builder.append(getTableName(container));
        builder.append(Statement.BRACKET_LEFT);
        return builder.toString();
    }

    /**
     * get primary key
     *
     * @param container
     * @return
     */
    public static String getPrimaryKeySQL(@NonNull Container container) {
        String primary = null;
        FieldContainer fieldContainer = container.getPrimaryField();
        if (fieldContainer == null) {
            fieldContainer = getPrimaryFieldContainer(container);
            container.setPrimaryField(fieldContainer);
        }
        if (fieldContainer != null) {
            String name = fieldContainer.getField().getName();
            primary = getPrimaryString(name, fieldContainer.isAuto());
        } else {
            boolean checkPrimaryKey = CouSyncDb.Config.isCheckPrimaryKey();

            PrimaryKey primaryKey = container.getPrimaryKey();
            if ((primaryKey == null || TextUtils.isEmpty(primaryKey.keyName())) && checkPrimaryKey)
                throw new NoPrimaryKeyException("the class " + container.getModelName() +
                        " do not set primary key");
            else if (primaryKey != null && !TextUtils.isEmpty(primaryKey.keyName()))
                primary = getPrimaryString(primaryKey.keyName(), primaryKey.autoIncrement());
        }
        return primary;
    }

    /**
     * column type primary key (autoincrement)
     *
     * @param name
     * @param isAuto
     * @return
     */
    private static String getPrimaryString(@NonNull String name, boolean isAuto) {
        String primary = name + Statement.INT + Statement.PRIMARY_KEY;
        if (isAuto)
            primary += Statement.AUTOINCREMENT;
        primary += Statement.COMMA;
        return primary;
    }

    /**
     * column type,
     * if boolean: column integer(1);
     *
     * @param field
     * @return null 不是基本类型
     */
    private static String getTypeString(@NonNull Field field) {
        String sql = null;
        Class clazz = field.getType();
        String type = SqlUtil.getTypeOfClass(clazz);
        if (TextUtils.isEmpty(type)) {
            LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + field.getName() + "-" + field.getType() +
                    " parse type error");
            return null;
        }
        sql = field.getName() + type;
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class))
            sql += Statement.BRACKET_LEFT + 1 + Statement.BRACKET_RIGHT;
        return sql;
    }

    /**
     * 先根据TYPE注解获取FieldContainer，如果null再根据Field获取
     * 都取不到return null
     *
     * @param container
     * @return
     */
    static FieldContainer getPrimaryFieldContainer(@NonNull Container container) {

        HashMap<String, FieldContainer> containers = container.getFieldContainers();
        if (containers == null || containers.size() < 1) {
            return null;
        }

        PrimaryKey primaryKey = container.getPrimaryKey();
        if (primaryKey != null) {//先根据TYPE注解获取FieldContainer
            String name = primaryKey.keyName();
            boolean isAuto = primaryKey.autoIncrement();
            if (!TextUtils.isEmpty(name)) {
                FieldContainer fc = containers.get(name);
                if (fc != null) {
                    fc.setIsAuto(isAuto);
                    fc.setIsPrimaryKey(true);
                    return fc;
                }
            }
        }

        //Field获取
        Set<Map.Entry<String, FieldContainer>> sets = containers.entrySet();
        Iterator<Map.Entry<String, FieldContainer>> iterator = sets.iterator();
        while ((iterator.hasNext())) {
            Map.Entry<String, FieldContainer> entry = iterator.next();
            FieldContainer fieldContainer = entry.getValue();
            if (fieldContainer.isPrimaryKey()) {
                return fieldContainer;
            }
        }
        return null;
    }
}
