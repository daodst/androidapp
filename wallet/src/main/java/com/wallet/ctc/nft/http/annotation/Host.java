

package com.wallet.ctc.nft.http.annotation;


import com.wallet.ctc.nft.http.helper.HttpHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Host {
    String value() default HttpHelper.HOST_DEFAULT;
}
