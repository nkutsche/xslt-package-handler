<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:nk="http://www.nkutsche.com/example"
    exclude-result-prefixes="xs math"
    version="3.0">
    <xsl:use-package name="com.nkutsche.example.lib" package-version="1.0.0"/>
    
    <xsl:template name="xsl:initial-template">
        <root processor="{system-property('xsl:product-name')} {system-property('xsl:product-version')}">
            <xsl:sequence select="nk:foo()"/>
        </root>
    </xsl:template>
    
</xsl:stylesheet>