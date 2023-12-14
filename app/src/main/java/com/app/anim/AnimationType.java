package com.app.anim;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Retention(SOURCE)
@Target({PARAMETER})
@IntDef(value = {AnimationType.TYPE_POS_DEVICES, AnimationType.TYPE_SALARY, AnimationType.TYPE_DVM_VIRTUAL, AnimationType.TYPE_AIRDROP})
public @interface AnimationType {
    public static final int TYPE_POS_DEVICES = 0;
    public static final int TYPE_DVM_VIRTUAL = 1;
    public static final int TYPE_SALARY = 2;
    public static final int TYPE_AIRDROP = 3;
}
