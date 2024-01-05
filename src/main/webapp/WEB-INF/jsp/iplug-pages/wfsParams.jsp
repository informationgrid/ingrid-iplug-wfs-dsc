<%--
  **************************************************-
  ingrid-iplug-wfs-dsc:war
  ==================================================
  Copyright (C) 2014 - 2024 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.2 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  https://joinup.ec.europa.eu/software/page/eupl
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<%@ include file="/WEB-INF/jsp/base/include.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="WfsConfig.main.configuration"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<link rel="StyleSheet" href="../css/base/portal_u.css" type="text/css" media="all" />

</head>
<body>
    <div id="header">
        <img src="../images/base/logo.gif" width="168" height="60" alt="Portal" />
        <h1><fmt:message key="WfsConfig.main.configuration"/></h1>
        <security:authorize access="isAuthenticated()">
            <div id="language"><a href="../base/auth/logout.html"><fmt:message key="WfsConfig.main.logout"/></a></div>
        </security:authorize>
    </div>
    <div id="help"><a href="#">[?]</a></div>

    <c:set var="active" value="wfsParams" scope="request"/>
    <c:import url="../base/subNavi.jsp"></c:import>

    <div id="contentBox" class="contentMiddle">
        <h1 id="head">WFS Parameter</h1>
        <div class="controls">
            <a href="../base/extras.html">Zur&uuml;ck</a>
            <a href="../base/welcome.html">Abbrechen</a>
            <a href="#" onclick="document.getElementById('wfsConfig').submit();">Weiter</a>
        </div>
        <div class="controls cBottom">
            <a href="../base/extras.html">Zur&uuml;ck</a>
            <a href="../base/welcome.html">Abbrechen</a>
            <a href="#" onclick="document.getElementById('wfsConfig').submit();">Weiter</a>
        </div>
        <div id="content">
            <form:form method="post" action="wfsParams.html" modelAttribute="wfsConfig">
                <input type="hidden" name="action" value="submit" />
                <input type="hidden" name="id" value="" />
                <table id="konfigForm">
                    <br />
                    <tr>
                        <td colspan="2"><h3><fmt:message key="WfsConfiguration.serviceUrl.chooseParameter"/></h3></td>
                    </tr>
                    <tr>
                        <td class="leftCol">Service Url</td>
                        <td>
                            <div class="input full"><form:input path="serviceUrl" /></div>
                            <form:errors path="serviceUrl" cssClass="error" element="div" />
                            <br />
                            <fmt:message key="WfsConfiguration.serviceUrl.help"/>
                            <p style="color: gray;"><fmt:message key="WfsConfiguration.serviceUrl.help.example"/></p>
                        </td>
                    </tr>
                    <tr>
                        <td class="leftCol">Anzahl Features in Detaildarstellung</td>
                        <td>
                            <div class="input full"><form:input path="featurePreviewLimit" /></div>
                            <form:errors path="featurePreviewLimit" cssClass="error" element="div" />
                            <br />
                            <fmt:message key="WfsConfiguration.featurePreviewLimit.help"/>
                            <p style="color: gray;"><fmt:message key="WfsConfiguration.featurePreviewLimit.help.example"/></p>
                        </td>
                    </tr>
                </table>
            </form:form>
        </div>
    </div>

    <div id="footer" style="height:100px; width:90%"></div>
</body>
</html>

