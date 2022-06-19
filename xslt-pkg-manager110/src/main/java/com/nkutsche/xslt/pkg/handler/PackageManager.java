package com.nkutsche.xslt.pkg.handler;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.ProxyReceiver;
import net.sf.saxon.event.Sender;
import net.sf.saxon.event.Sink;
import net.sf.saxon.lib.Initializer;
import net.sf.saxon.lib.ParseOptions;
import net.sf.saxon.om.AttributeMap;
import net.sf.saxon.om.NamespaceMap;
import net.sf.saxon.om.NodeName;
import net.sf.saxon.s9api.Location;
import net.sf.saxon.trans.CompilerInfo;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.trans.packages.PackageDetails;
import net.sf.saxon.trans.packages.PackageLibrary;
import net.sf.saxon.trans.packages.VersionedPackageName;
import net.sf.saxon.type.SchemaType;
import top.marchand.xml.protocols.ProtocolInstaller;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;


public class PackageManager implements Initializer {

    @Override
    public void initialize(Configuration config) throws TransformerException {
        init(config);
    }

    public static void init(Configuration config){
//        verify that cp: protocol works for URL lookups into classpath resources
        ProtocolInstaller.registerAdditionalProtocols();

        PackageFinder110.loadPackageLibrary(config);

    }

}