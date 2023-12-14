// Copyright (C) 2011 - Will Glozer.  All rights reserved.

package com.lambdaworks.jni;


public class SysLibraryLoader implements LibraryLoader {
    
    public boolean load(String name, boolean verify) {
        boolean loaded;

        try {
            System.loadLibrary(name);
            loaded = true;
        } catch (Throwable e) {
            loaded = false;
        }

        return loaded;
    }
}
