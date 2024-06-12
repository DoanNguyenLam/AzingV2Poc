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
<%--        <label class="aui-field-label" for="<portlet:namespace />script">--%>
<%--            <spring:message code="gg-client-key"></spring:message>--%>
<%--        </label>--%>
<%--        <aui:fieldset>--%>
<%--            <aui:input label="" name="googleClientKey" cssClass="w-100" value="<%=ggClientKey%>"/>--%>
<%--        </aui:fieldset>--%>

<%--        &lt;%&ndash; GG Secret key &ndash;%&gt;--%>
<%--        <label class="aui-field-label" for="<portlet:namespace />script">--%>
<%--            <spring:message code="gg-secret-key"></spring:message>--%>
<%--        </label>--%>
<%--        <aui:fieldset>--%>
<%--            <aui:input label="" type="password" name="googleSecretKey" cssClass="w-100" value="<%=ggSecretKey%>"/>--%>
<%--        </aui:fieldset>--%>

        <%-- GG Access token key --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="gg-access-token"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="ggAccessToken" cssClass="w-100" value="<%=ggAccessToken%>"/>
        </aui:fieldset>

        <%-- Claude API KEY --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="claude-api-key"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="claudeAPIKey" cssClass="w-100" value="<%=claudeAPIKey%>"/>
        </aui:fieldset>

        <%-- Claude API KEY --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="is-use-claude-ai"></spring:message>
        </label>
        <aui:fieldset>
            <aui:select label="" name="isUseClaudeAI" cssClass="w-50" value="<%= isUseClaudeAI %>">
                <aui:option value="true" label="Enable"></aui:option>
                <aui:option value="false" label="Disable"></aui:option>
            </aui:select>
        </aui:fieldset>


        <%-- Prompt Summary Single mail--%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="prompt-summary-single-mail"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input type="textarea" label="" name="promptSummarySingleMail" cssClass="w-100" value="<%=promptSummarySingleMail%>"/>
        </aui:fieldset>

        <%-- Prompt Suggestion Single mail--%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="prompt-suggestion-single-mail"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input type="textarea" label="" name="promptSuggestionSingleMail" cssClass="w-100" value="<%=promptSuggestionSingleMail%>"/>
        </aui:fieldset>

        <%-- Prompt Summary Single mail--%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="prompt-summary-conversation"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input type="textarea" label="" name="" cssClass="w-100" value="<%=promptSummaryConversation%>"/>
        </aui:fieldset>

        <%-- Prompt Suggestion Single mail--%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="prompt-suggestion-conversation"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input type="textarea" label="" name="promptSuggestionSingleMail" cssClass="w-100" value="<%=promptSuggestionConversation%>"/>
        </aui:fieldset>


        <aui:button-row>
            <aui:button type="submit"></aui:button>
        </aui:button-row>
    </aui:form>
</div>

