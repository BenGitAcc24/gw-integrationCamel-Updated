<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" encoding="UTF-8"/>

  <!-- Expected input:
    <GuidewireRequest>
      <Policy>
        <Insured>
          <Address>
            <Street>123 Main St</Street>
            <City>Chino</City>
            <State>CA</State>
            <Zip>91710</Zip>
          </Address>
        </Insured>
      </Policy>
    </GuidewireRequest>
  -->

  <xsl:template match="/">
    <xsl:text>{</xsl:text>
    <xsl:text>"street":"</xsl:text><xsl:value-of select="//Address/Street"/><xsl:text>",</xsl:text>
    <xsl:text>"city":"</xsl:text><xsl:value-of select="//Address/City"/><xsl:text>",</xsl:text>
    <xsl:text>"state":"</xsl:text><xsl:value-of select="//Address/State"/><xsl:text>",</xsl:text>
    <xsl:text>"zip":"</xsl:text><xsl:value-of select="//Address/Zip"/><xsl:text>"}</xsl:text>
  </xsl:template>
</xsl:stylesheet>
