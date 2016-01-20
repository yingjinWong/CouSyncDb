package cn.dengx.cousyncdb.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/12,11:46.
 * <p/>
 * <p/>
 * 创建表
 */

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    /**
     * 表名
     *
     * @return
     */
    String Name();


//    /**
//     * 最大保存行数
//     *
//     * @return
//     */
//    int maxRow();

}
