package com.megvii.mapapi.annotation;

import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RecordVisitNum {
}
