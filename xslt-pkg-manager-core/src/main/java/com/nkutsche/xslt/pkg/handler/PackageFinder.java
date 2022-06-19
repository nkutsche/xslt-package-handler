package com.nkutsche.xslt.pkg.handler;

import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public abstract class PackageFinder<P> {

    public static final String PACKAGE_INFO_PATH = "META-INF/xslt/com.nkutsche.xslt.packages";

    private final ClassLoader classLoader;

    public PackageFinder(ClassLoader classLoader){

        this.classLoader = classLoader;
    }

    public ArrayList<P> search(){
        ArrayList<P> packageList = new ArrayList<>();
        try {
            Enumeration<URL> packageInfos = classLoader.getResources(PACKAGE_INFO_PATH);

            while (packageInfos.hasMoreElements()){
                packageList.addAll(find(packageInfos.nextElement()));
            }

        } catch (Exception e){

        }

        return packageList;
    }

    public ArrayList<P> find(URL srcUrl) throws IOException {
        ArrayList<P> packages = new ArrayList<>();

        if(srcUrl == null) {
            return packages;
        }
        List<String> lines = readLines(srcUrl);

        for (String pkgpath:
             lines) {
            pkgpath = pkgpath.startsWith("/") ? pkgpath : "/" + pkgpath;

            URL pkgUrl = PackageFinder.class.getResource(pkgpath);

            if(pkgUrl == null)
                continue;
            try {
                packages.add(find(pkgUrl, pkgUrl.toString()));
            } catch (IOException e){
                continue;
            }
        }
        return packages;
    }

    public abstract P find(URL packageUrl, String systemId) throws IOException;



    private List<String> readLines(URL url) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        }
    }
}
