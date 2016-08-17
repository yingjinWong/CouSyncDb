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
 * <p/>
 * 唯一键
 * <p/>
 * set unique key
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD})
public @interface Unique {
}
