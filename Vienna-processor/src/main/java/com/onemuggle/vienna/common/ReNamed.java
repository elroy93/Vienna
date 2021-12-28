package com.onemuggle.vienna.common;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE})
public @interface ReNamed {
    /**
     * 重命名的名称
     *
     * @return
     */
    String value();

    /**
     * 是实现接口还是继承类
     */
    boolean isInterface();
}
