package com.nkutsche.xslt.pkg.handler;


import net.sf.saxon.Configuration;
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.ProxyReceiver;
import net.sf.saxon.event.Sender;
import net.sf.saxon.event.Sink;
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

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URL;

class PackageFinder110 extends PackageFinder<PackageDetails> {

    private static PackageFinder110 packageFinder = null;

    private Configuration config;

    PackageFinder110(Configuration config){
        super(PackageFinder110.class.getClassLoader());
        this.config = config;
    }

    @Override
    public PackageDetails find(URL packageUrl, String systemId, String resourcePath) throws IOException {
        PackageInspector inspector = new PackageInspector(config.makePipelineConfiguration());

        try {
            ParseOptions options = new ParseOptions();
            options.setDTDValidationMode(4);
            options.setSchemaValidationMode(4);
            Sender.send(new StreamSource(packageUrl.openStream(), "cp:" + resourcePath), inspector, new ParseOptions());
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

            details.sourceLocation = new StreamSource(packageUrl.openStream(), "cp:" + resourcePath);

            return details;
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


    public static void loadPackageLibrary(Configuration config){
        CompilerInfo compInfo = config.getDefaultXsltCompilerInfo();

        PackageLibrary pkgLib = compInfo.getPackageLibrary();
        if(pkgLib == null){
            pkgLib = new PackageLibrary(compInfo);
        }

        if(packageFinder == null){
            packageFinder = new PackageFinder110(config);
        }

        for (PackageDetails pkg: packageFinder.getPackages()) {
            pkgLib.addPackage(pkg);
        }

        compInfo.setPackageLibrary(pkgLib);
    }
}