package com.nkutsche.xslt.pkg.handler;

import com.xmlcalabash.config.*;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.util.DefaultJaxpConfigurer;
import com.xmlcalabash.util.DefaultJingConfigurer;
import com.xmlcalabash.util.DefaultSaxonConfigurer;
import com.xmlcalabash.util.DefaultXMLCalabashConfigurer;
import net.sf.saxon.Configuration;

public class XProcConfigurer implements com.xmlcalabash.config.XProcConfigurer {

    private final XProcRuntime runtime;

    public XProcConfigurer(XProcRuntime runtime){
        this.runtime = runtime;
    }

    @Override
    public XMLCalabashConfigurer getXMLCalabashConfigurer() {
        return new DefaultXMLCalabashConfigurer(runtime);
    }

    @Override
    public SaxonConfigurer getSaxonConfigurer() {
        return new SaxonConfigurer() ;
    }

    @Override
    public JingConfigurer getJingConfigurer() {
        return new DefaultJingConfigurer();
    }

    @Override
    public JaxpConfigurer getJaxpConfigurer() {
        return new DefaultJaxpConfigurer();
    }

    private class SaxonConfigurer extends DefaultSaxonConfigurer {
        @Override
        public void configXSLT(Configuration config) {
            PackageManager.init(config);
        }
    }
}
