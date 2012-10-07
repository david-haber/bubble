<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h3>Sign Up</h3>
<p>Before you use Bubble, please confirm your details. This will
	help us to improve Bubble's performance.</p>
<c:url value="/signup" var="signupUrl" />
<form:form id="signup" action="${signupUrl}" method="post"
	modelAttribute="signupForm">
	<div class="formInfo">
		<s:bind path="*">
			<c:choose>
				<c:when test="${status.error}">
					<div class="error">Unable to register. Please make sure you have filled in all fields.</div>
				</c:when>
			</c:choose>
		</s:bind>
	</div>

	<fieldset>
		<table>
			<tr>
				<td><form:label path="firstName">First Name <form:errors
							path="firstName" cssClass="error" />
					</form:label></td>
				<td><form:input path="firstName" /></td>
			</tr>
			<tr>
				<td><form:label path="lastName">Last Name <form:errors
							path="lastName" cssClass="error" />
					</form:label></td>
				<td><form:input path="lastName" /></td>
			</tr>
			<tr>
				<td><form:label path="username">Username <form:errors
							path="username" cssClass="error" />
					</form:label></td>
				<td><form:input path="username" /></td>
			</tr>
			<tr>
				<td><form:label path="email">E-Mail <form:errors
							path="email" cssClass="error" />
					</form:label></td>
				<td><form:input path="email" /></td>
			</tr>
			<tr>
				<td><form:label path="age">Age <form:errors
							path="age" cssClass="error" />
					</form:label></td>
				<td><form:input path="age" /></td>
			</tr>
			<tr>
				<td><form:label path="location">Location <form:errors
							path="location" cssClass="error" />
					</form:label></td>
				<td><form:input path="location" /></td>
			</tr>
		</table>
	</fieldset>
	<p>
		<button type="submit">Sign Up</button>
	</p>
</form:form>
