<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="email.dto.ModalAI" %>
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
            <aui:input label="" type="password" name="googleClientKey" cssClass="w-100" value="<%=ggClientKey%>"/>
        </aui:fieldset>

        <%-- GG Secret key --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="gg-secret-key"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="googleSecretKey" cssClass="w-100" value="<%=ggSecretKey%>"/>
        </aui:fieldset>

        <%-- GG Access token key --%>
<%--        <label class="aui-field-label" for="<portlet:namespace />script">--%>
<%--            <spring:message code="gg-access-token"></spring:message>--%>
<%--        </label>--%>
<%--        <aui:fieldset>--%>
<%--            <aui:input label="" type="password" name="ggAccessToken" cssClass="w-100" value="<%=ggAccessToken%>"/>--%>
<%--        </aui:fieldset>--%>

        <%-- GG Access token key --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="gg-refresh-token"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="ggRefreshToken" cssClass="w-100" value="<%=ggRefressToken%>"/>
        </aui:fieldset>

        <%-- GG Access token key --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="gg-refresh-token"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="ggRefreshToken" cssClass="w-100" value="<%=ggRefressToken%>"/>
        </aui:fieldset>

        <%-- Claude API KEY --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="claude-api-key"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="claudeAPIKey" cssClass="w-100" value="<%=claudeAPIKey%>"/>
        </aui:fieldset>

        <%-- CHAT GPT API KEY --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="gpt-api-key"></spring:message>
        </label>
        <aui:fieldset>
            <aui:input label="" type="password" name="gptAPIKey" cssClass="w-100" value="<%=gptAPIKey%>"/>
        </aui:fieldset>

        <%-- SELECT MODAL --%>
        <label class="aui-field-label" for="<portlet:namespace />script">
            <spring:message code="select-modal"></spring:message>
        </label>
        <aui:fieldset>
            <aui:select label="" name="modal" cssClass="w-50" value="<%= modal %>">
                <aui:option value="<%= ModalAI.CHAT_GPT %>" label="GPT-3.5 Turbo"></aui:option>
                <aui:option value="<%= ModalAI.CLAUDE_AI %>" label="Claude 3 Haiku"></aui:option>
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

