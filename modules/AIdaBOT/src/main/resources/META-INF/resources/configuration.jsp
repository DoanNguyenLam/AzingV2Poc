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

        <%-- URL --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="url"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" name="url" cssClass="w-100" value="<%=url%>"/>
        </aui:fieldset>

        <%-- Script --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="script"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" name="script" cssClass="w-100" value="<%=script%>"/>
        </aui:fieldset>


        <aui:button-row>
            <aui:button type="submit"></aui:button>
        </aui:button-row>
    </aui:form>
</div>

