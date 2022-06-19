<?xml version="1.0" encoding="UTF-8"?>
<xsl:package xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:nk="http://www.nkutsche.com/example"
    exclude-result-prefixes="xs math"
    name="com.nkutsche.example.lib"
    package-version="1.0.0"
    version="3.0">
    
    <xsl:function name="nk:foo" visibility="public">
        <xsl:sequence select="'bar'"/>
    </xsl:function>
    
</xsl:package>