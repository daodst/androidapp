// Copyright (C) 2013 - Will Glozer.  All rights reserved.

package com.lambdaworks.jni;


public class NilLibraryLoader implements LibraryLoader {
    
    public boolean load(String name, boolean verify) {
        return false;
    }
}
