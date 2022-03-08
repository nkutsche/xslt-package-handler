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
//        verify that cp: protocol works for URL lookups into classpath resources
        ProtocolInstaller.registerAdditionalProtocols();

        CompilerInfo compInfo = config.getDefaultXsltCompilerInfo();

        PackageLibrary pkgLib = new PackageLibrary(compInfo);
        PackageFinder110 packageFinder = new PackageFinder110(config);

        for (PackageDetails pkg: packageFinder.search()) {
            pkgLib.addPackage(pkg);
        }

        compInfo.setPackageLibrary(pkgLib);
    }

    private class PackageFinder110 extends PackageFinder<PackageDetails> {

        private Configuration config;

        PackageFinder110(Configuration config){

            this.config = config;
        }

        @Override
        public PackageDetails find(URL packageUrl, String systemId) throws IOException {
            PackageInspector inspector = new PackageInspector(config.makePipelineConfiguration());

            try {
                ParseOptions options = new ParseOptions();
                options.setDTDValidationMode(4);
                options.setSchemaValidationMode(4);
                Sender.send(new StreamSource(packageUrl.openStream(), systemId), inspector, new ParseOptions());
            } catch (XPathException e) {
                if (!e.getMessage().equals("#start#")) {
                    e.printStackTrace();
                    throw new IOException(e.getMessage());
                }
            }

            VersionedPackageName vp = inspector.getNameAndVersion();
            if (vp == null) {
                return null;
            } else {
                PackageDetails details = new PackageDetails();
                details.nameAndVersion = vp;

                details.sourceLocation = new StreamSource(packageUrl.openStream(), systemId);

                return details;
            }
        }
    }


    private class PackageInspector extends ProxyReceiver {
        private boolean isSefFile;
        private String packageName;
        private String packageVersion = "1";
        private int elementCount = 0;

        private PackageInspector(PipelineConfiguration pipe) {
            super(new Sink(pipe));
        }

        public void startElement(NodeName elemName, SchemaType type, AttributeMap attributes, NamespaceMap namespaces, Location location, int properties) throws XPathException {
            if (this.elementCount++ >= 1) {
                throw new XPathException("#start#");
            } else {
                if (attributes.get("", "name") != null) {
                    this.packageName = attributes.get("", "name").getValue();
                }

                if (attributes.get("", "package-version") != null) {
                    this.packageVersion = attributes.get("", "package-version").getValue();
                }

                if (attributes.get("", "packageVersion") != null) {
                    this.packageVersion = attributes.get("", "packageVersion").getValue();
                }

            }
        }

        private VersionedPackageName getNameAndVersion() {
            if (this.packageName == null) {
                return null;
            } else {
                try {
                    return new VersionedPackageName(this.packageName, this.packageVersion);
                } catch (XPathException var2) {
                    return null;
                }
            }
        }

    }

}