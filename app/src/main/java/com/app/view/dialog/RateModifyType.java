package com.app.view.dialog;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Retention(SOURCE)
@Target({PARAMETER})
@IntDef(value = {RateModifyType.TYPE_BROKERAGE, RateModifyType.TYPE_SALARY})
public @interface RateModifyType {
    
    public static final int TYPE_BROKERAGE = 0;
    
    public static final int TYPE_SALARY = 1;
}
