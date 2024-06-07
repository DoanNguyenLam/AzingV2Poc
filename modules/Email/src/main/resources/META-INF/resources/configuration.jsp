<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="./init.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<liferay-portlet:actionURL
        portletConfiguration="<%= true %>"
        var="configurationActionURL"
/>

<liferay-portlet:renderURL
        portletConfiguration="<%= true %>"
        var="configurationRenderURL"
/>

<div style="padding: 1rem 1rem 5rem;">
    <aui:form action="<%= configurationActionURL %>" method="post" name="fm">
        <aui:input
                name="<%= Constants.CMD %>"
                type="hidden"
                value="<%= Constants.UPDATE %>"
        />

        <aui:input
                name="redirect"
                type="hidden"
                value="<%= configurationRenderURL %>"
        />

        <%-- GG Client key --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="gg-client-key"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" name="googleClientKey" cssClass="w-100" value="<%=ggClientKey%>"/>
        </aui:fieldset>

        <%-- GG Secret key --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="gg-secret-key"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="googleSecretKey" cssClass="w-100" value="<%=ggSecretKey%>"/>
        </aui:fieldset>

        <%-- Claude API key --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="claude-api-key"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="claudeAPIKey" cssClass="w-100" value="<%=claudeAPIKey%>"/>
        </aui:fieldset>

        <%-- GG Access token key --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="gg-access-token"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="ggAccessToken" cssClass="w-100" value="<%=ggAccessToken%>"/>
        </aui:fieldset>


        <aui:button-row>
            <aui:button type="submit"></aui:button>
        </aui:button-row>
    </aui:form>
</div>

