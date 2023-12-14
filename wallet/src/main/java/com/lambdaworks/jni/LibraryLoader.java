// Copyright (C) 2011 - Will Glozer.  All rights reserved.

package com.lambdaworks.jni;


public interface LibraryLoader {
    
    boolean load(String name, boolean verify);
}
