// Copyright (C) 2011 - Will Glozer.  All rights reserved.

package com.lambdaworks.jni;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class JarLibraryLoader implements LibraryLoader {
    private final CodeSource codeSource;
    private final String libraryPath;

    
    public JarLibraryLoader() {
        this(JarLibraryLoader.class.getProtectionDomain().getCodeSource(), "lib");
    }

    
    public JarLibraryLoader(CodeSource codeSource, String libraryPath) {
        this.codeSource  = codeSource;
        this.libraryPath = libraryPath;
    }

    
    public boolean load(String name, boolean verify) {
        boolean loaded = false;

        try {
            Platform platform = Platform.detect();
            JarFile jar = new JarFile(codeSource.getLocation().getPath(), verify);
            try {
                for (String path : libCandidates(platform, name)) {
                    JarEntry entry = jar.getJarEntry(path);
                    if (entry == null) continue;

                    File lib = extract(name, jar.getInputStream(entry));
                    System.load(lib.getAbsolutePath());
                    lib.delete();

                    loaded = true;
                    break;
                }
            } finally {
                jar.close();
            }
        } catch (Throwable e) {
            loaded = false;
        }

        return loaded;
    }

    
    private static File extract(String name, InputStream is) throws IOException {
        byte[] buf = new byte[4096];
        int len;

        File lib = File.createTempFile(name, "lib");
        FileOutputStream os = new FileOutputStream(lib);

        try {
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
            lib.delete();
            throw e;
        } finally {
            os.close();
            is.close();
        }

        return lib;
    }

    
    private List<String> libCandidates(Platform platform, String name) {
        List<String> candidates = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        sb.append(libraryPath).append("/");
        sb.append(platform.arch).append("/");
        sb.append(platform.os).append("/");
        sb.append("lib").append(name);

        switch (platform.os) {
            case darwin:
                candidates.add(sb + ".dylib");
                candidates.add(sb + ".jnilib");
                break;
            case linux:
            case freebsd:
                candidates.add(sb + ".so");
                break;
        }

        return candidates;
    }
}
