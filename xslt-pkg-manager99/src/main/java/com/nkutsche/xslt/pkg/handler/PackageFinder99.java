package com.nkutsche.xslt.pkg.handler;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.ProxyReceiver;
import net.sf.saxon.event.Sender;
import net.sf.saxon.event.Sink;
import net.sf.saxon.expr.parser.Location;
import net.sf.saxon.lib.ParseOptions;
import net.sf.saxon.om.NodeName;
import net.sf.saxon.trans.CompilerInfo;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.trans.packages.PackageDetails;
import net.sf.saxon.trans.packages.PackageLibrary;
import net.sf.saxon.trans.packages.VersionedPackageName;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.type.SimpleType;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URL;

public class PackageFinder99 extends  PackageFinder<PackageDetails> {

    private static PackageFinder99 packageFinder = null;

    private Configuration config;

    PackageFinder99(Configuration config){
        super(PackageFinder99.class.getClassLoader());
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

    private class PackageInspector extends ProxyReceiver {
        private boolean isSefFile;
        private String packageName;
        private String packageVersion = "1";
        private int elementCount = 0;

        private PackageInspector(PipelineConfiguration pipe) {
            super(new Sink(pipe));
        }

        public void startElement(NodeName namecode, SchemaType typecode, Location location, int properties) throws XPathException {
            if (this.elementCount++ >= 1) {
                throw new XPathException("#start#");
            } else {
            }
        }

        public void attribute(NodeName name, SimpleType typeCode, CharSequence value, Location locationId, int properties) throws XPathException {
            if (name.getLocalPart().equals("name")) {
                this.packageName = value.toString();
            } else if (name.getLocalPart().equals("package-version") || name.getLocalPart().equals("packageVersion")) {
                this.packageVersion = value.toString();
            } else {
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
            packageFinder = new PackageFinder99(config);
        }

        for (PackageDetails pkg: packageFinder.getPackages()) {
            pkgLib.addPackage(pkg);
        }

        compInfo.setPackageLibrary(pkgLib);
    }
}
