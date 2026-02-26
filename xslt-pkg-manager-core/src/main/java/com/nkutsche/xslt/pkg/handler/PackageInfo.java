package com.nkutsche.xslt.pkg.handler;

import java.io.IOException;
import java.net.URL;

public class PackageInfo {
    String name, version, systemId;
    URL location;

    public PackageInfo verify() throws IOException {
        if(name == null || version == null){
            throw new IOException("Could not extract name and version of package " + location);
        }
        return this;
    }
}
