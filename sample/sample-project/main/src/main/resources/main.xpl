<?xml version="1.0" encoding="UTF-8"?>
<p:declare-step xmlns:p="http://www.w3.org/ns/xproc" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:c="http://www.w3.org/ns/xproc-step" version="1.0">
    <p:output port="result" primary="true"/>
    <p:xslt template-name="xsl:initial-template">
        <p:input port="source">
            <p:empty/>
        </p:input>
        <p:input port="parameters">
            <p:empty/>
        </p:input>
        <p:input port="stylesheet">
            <p:document href="main.xsl"/>
        </p:input>
    </p:xslt>
</p:declare-step>