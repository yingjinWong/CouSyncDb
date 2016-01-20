package cn.dengx.cousyncdb.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/12,11:58.
 *
 * 主键注解
 *
 * if a table bean has no PrimaryKey and CouSyncDb.Config.checkPrimaryKey is true;there will throw
 * a NoPrimaryKeyException
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD,ElementType.TYPE})
public @interface PrimaryKey {


    /**
     * bean里必须含有名字一样的field
     * @return
     */
    String keyName();

    /**
     * 是否自增长
     *
     * @return
     */
    boolean autoIncrement() default false;
}
