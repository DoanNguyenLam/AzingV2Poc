<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="static email.configs.EmailConfigKeys.*" %>

<liferay-theme:defineObjects/>

<portlet:defineObjects/>

<%
    // Init load portlet setting
    String ggClientKey = portletPreferences.getValue(GG_CLIENT_KEY, "");
    String ggSecretKey = portletPreferences.getValue(GG_SECRET_KEY, "");
    String claudeAPIKey = portletPreferences.getValue(CLAUDE_API_KEY, "");
%>
