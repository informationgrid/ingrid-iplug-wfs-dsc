<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:idf="http://www.portalu.de/IDF/1.0"
	xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gml="http://www.opengis.net/gml">
	<xsl:output method="xml" />

  <xsl:template match="/">
    <xsl:apply-templates select="@*|node()"/>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="gmd:MD_Metadata">
    <idf:idfMdMetadata>
      <xsl:apply-templates select="@*|node()" />
    </idf:idfMdMetadata>
  </xsl:template>

  <xsl:template match="gmd:CI_ResponsibleParty">
    <idf:idfResponsibleParty>
      <xsl:apply-templates select="@*|node()" />
    </idf:idfResponsibleParty>
  </xsl:template>

</xsl:stylesheet>