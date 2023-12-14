// Copyright (C) 2011 - Will Glozer.  All rights reserved.

package com.lambdaworks.jni;


public class LibraryLoaders {
    
    public static LibraryLoader loader() {
        String type = System.getProperty("com.lambdaworks.jni.loader");

        if (type != null) {
            if (type.equals("sys")) return new SysLibraryLoader();
            if (type.equals("nil")) return new NilLibraryLoader();
            if (type.equals("jar")) return new JarLibraryLoader();
            throw new IllegalStateException("Illegal value for com.lambdaworks.jni.loader: " + type);
        }

        String vmSpec = System.getProperty("java.vm.specification.name");
        return vmSpec.startsWith("Java") ? new JarLibraryLoader() : new SysLibraryLoader();
    }
}
