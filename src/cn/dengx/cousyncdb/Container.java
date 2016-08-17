package cn.dengx.cousyncdb;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import cn.dengx.cousyncdb.Exceptions.CouSyncDbException;
import cn.dengx.cousyncdb.annotations.PrimaryKey;
import cn.dengx.cousyncdb.annotations.Table;
import cn.dengx.cousyncdb.util.BeanUtil;
import cn.dengx.cousyncdb.util.LogUtil;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/12,14:04.
 */
public class Container {

    /**
     * 含有包名
     */
    private String modelName;

    private Class modelClass;
    private HashMap<String, FieldContainer> fieldContainers;
    private Field[] fields;
    private Table table;
    private PrimaryKey primaryKey;
    private FieldContainer primaryField;

    private boolean tableChecked;

    public Container() {
    }

    public HashMap<String, FieldContainer> getFieldContainers() {
        return fieldContainers;
    }

    public void setFieldContainers(HashMap<String, FieldContainer> fieldContainers) {
        this.fieldContainers = fieldContainers;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Class getModelClass() {
        return modelClass;
    }

    public void setModelClass(Class modelClass) {
        this.modelClass = modelClass;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public boolean isTableChecked() {
        return tableChecked;
    }

    public void setTableChecked(boolean tableChecked) {
        this.tableChecked = tableChecked;
    }

    public FieldContainer getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(FieldContainer primaryField) {
        this.primaryField = primaryField;
    }

    public static Container create(@NonNull Class clazz) {
        Container container = new Container();

        Table table = (Table) clazz.getAnnotation(Table.class);
        PrimaryKey primaryKey = (PrimaryKey) clazz.getAnnotation(PrimaryKey.class);
        container.setTable(table);
        container.setPrimaryKey(primaryKey);
        container.setModelName(clazz.getName());
        container.setModelClass(clazz);

        HashMap<String, FieldContainer> fieldContainers = new HashMap<>();
        Field[] fields = null;
//        Method[] methods = null;
        while (!clazz.equals(Object.class)) {
            Field[] fs = clazz.getDeclaredFields();
            Method[] ms = clazz.getDeclaredMethods();
            ArrayList<Field> list = new ArrayList<>();

            for (Field f : fs) {
                String m = Modifier.toString(f.getModifiers());
                if (m.contains("static")) continue;//过滤静态变量
                String key = f.getName();
                String typeName = f.getType().getName();
                if (BeanUtil.baseType(typeName)) {//基本类型简单检测
                    fieldContainers.put(key, BeanUtil.create(f, ms));
                    list.add(f);
                } else
                    LogUtil.e(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "jump FieldContainer " + key +
                            " typeName=" + typeName + " in " + container.toString());
            }
            fs = list.toArray(new Field[list.size()]);
            if (fields == null) {
                fields = fs;
            } else {
                int desPos = fields.length;
                int length = desPos + fs.length;
//                fields = Arrays.copyOf(fields,length);
                Field[] newFields = new Field[length];
                System.arraycopy(fields, 0, newFields, 0, desPos);
                fields = newFields;
                System.arraycopy(fs, 0, fields, desPos, fs.length);
            }

//            if (methods == null) {
//                methods = clazz.getDeclaredMethods();
//            } else {
//                int desPos = methods.length;
//                int length = desPos + ms.length;
//                Method[] newMethods = new Method[length];
//                System.arraycopy(methods, 0, newMethods, 0, desPos);
//                methods = newMethods;
//                System.arraycopy(ms, 0, methods, desPos, ms.length);
//            }

            clazz = clazz.getSuperclass();
        }
        if (fieldContainers.size() > 0)
            container.setFieldContainers(fieldContainers);
        else {
            throw new CouSyncDbException("you must have get and set method on field");
        }
        container.setFields(fields);
        FieldContainer pri = CreateTableSQLBuilder.getPrimaryFieldContainer(container);
        container.setPrimaryField(pri);
        return container;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(modelClass.getName()).append("-");
        if (fields != null)
            builder.append("fields length =").append(fields.length);
        return builder.toString();
    }
}
