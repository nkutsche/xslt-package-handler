package com.nkutsche.xslt.pkg.handler;

import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

public abstract class PackageFinder<P> {



    public ArrayList<P> search(){
        ArrayList<P> packageList = new ArrayList<>();
        try {
            URL depUrl = new URL("cp:/META-INF/xslt/dependencies");
            InputStream depStream = depUrl.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(depStream));
            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();

                StringTokenizer st =
                        new StringTokenizer(line, ":");

                String groupId = null;
                String artifactId = null;

                if(st.hasMoreElements()){
                    String tk = st.nextToken();
                    groupId = "".equals(tk) ? null : tk;
                }
                if(st.hasMoreElements()){
                    String tk = st.nextToken();
                    artifactId = "".equals(tk) ? null : tk;
                }

                if(groupId != null && artifactId != null){
                    P pkg = find(groupId, artifactId);
                    if(pkg != null){
                        packageList.add(pkg);
                    }

                }


            }
        } catch (Exception e){

        }

        return packageList;
    }

    public P find(String groupId, String artifactId) throws IOException {
        URL srcUrl = PackageFinder.class.getResource("/META-INF/xslt/" + groupId + "/" + artifactId + "/packages");

        if(srcUrl == null)
            return null;

        Properties props = new Properties();
        props.load(srcUrl.openStream());

        String pkgpath = props.getProperty(groupId + "." + artifactId);
        pkgpath = pkgpath.startsWith("/") ? pkgpath : "/" + pkgpath;

        String systemId = "cp:" + pkgpath;

        URL pkgUrl = PackageFinder.class.getResource(pkgpath);
        try {
            return find(pkgUrl, systemId);
        } catch (IOException e){
            return null;
        }

    }

    public abstract P find(URL packageUrl, String systemId) throws IOException;
}
