package org.roklib.urifragmentrouting.parameter.annotation;

import java.lang.annotation.*;

/**
 * @author Roland Krüger
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CapturedParameter {
    String mapperName();
    String parameterName();
}
