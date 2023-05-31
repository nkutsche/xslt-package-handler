package com.nkutsche.xslt.pkg.handler;

import net.sf.saxon.Configuration;
import net.sf.saxon.lib.Initializer;
import top.marchand.xml.protocols.ProtocolInstaller;

import javax.xml.transform.TransformerException;


public class PackageManager implements Initializer {

    @Override
    public void initialize(Configuration config) throws TransformerException {
        init(config);
    }

    public static void init(Configuration config){
//        verify that cp: protocol works for URL lookups into classpath resources
        ProtocolInstaller.registerAdditionalProtocols();

        PackageFinder120.loadPackageLibrary(config);

    }

}