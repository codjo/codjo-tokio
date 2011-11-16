<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
           xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
           version="1.0"
           >
    <xsl:output method="html"/>

    <xsl:template match="/">
      <html><body>
         <xsl:apply-templates/>
      </body></html>
    </xsl:template>

    <xsl:template match="Scenarii">
        <h1 align="center">Test Fonctionnel
        <xsl:value-of select="@name"/> </h1><p/>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="Scenario">
        <hr/>
        <h2>Scenario : <xsl:value-of select="@id"/></h2>
        <xsl:value-of select="@comment"/> <br/>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="comment">
        <b>Commentaire : </b>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="properties">
        <h3>Propriétés Système</h3>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="etalon">
        <h3>Donnée Etalon</h3>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="input">
        <h3>Donnée en entrée</h3>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="table">
        <table border="0">
            <tr bgcolor="#DFDFFF" >
                <td><xsl:value-of select="@name"/></td>
            </tr>
            <tr><td><table border="0">
            <xsl:apply-templates/>
            </table></td></tr>
        </table>
    </xsl:template>

    <xsl:template match="row">
        <tr bgcolor="#FFFFCA" >
            <td bgcolor="#FFCC33">
                <b><xsl:if test="not(@id)">-</xsl:if><xsl:value-of select="@id"/></b>
                <xsl:if test="@inheritId"> (<xsl:value-of select="@inheritId"/>)</xsl:if>
                <xsl:if test="@comment"><i> : <xsl:value-of select="@comment"/></i></xsl:if>
            </td>
            <xsl:apply-templates/>
        </tr>
    </xsl:template>

    <xsl:template match="field">
        <td>
            <xsl:value-of select="@name"/> =
            <xsl:value-of select="@value"/>
        </td>
    </xsl:template>

</xsl:stylesheet>
