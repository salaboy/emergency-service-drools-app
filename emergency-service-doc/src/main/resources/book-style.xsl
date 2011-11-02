<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xslthl="http://xslthl.sf.net" xmlns:d="http://docbook.org/ns/docbook"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xslthl" version="1.0">

    <xsl:import href="urn:docbkx:stylesheet" />

    <!-- Make hyperlinks blue and don't display the underlying URL -->
    <xsl:param name="ulink.show" select="0" />

    <xsl:attribute-set name="xref.properties">
        <xsl:attribute name="color">red</xsl:attribute>
    </xsl:attribute-set>

	<xsl:param name="header.image.filename" select="media/logo.png"/>
    <!-- Add any other template overrides here -->

</xsl:stylesheet>