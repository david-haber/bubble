<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<html>
<head>
<title>Welcome to Bubble - Log in, sign up or learn more</title>
<style type="text/css">
#centre {
	margin-top: 300px;
	width: 700px;
	margin-left: auto;
	margin-right: auto;
	text-align: center;
}
</style>
</head>
<body>
	<div id="centre">
		<p>
			<img src="<c:url value="/resources/images/BubbleLogo.png"/>" />
		<p />
		<form id="tw_signin" action="<c:url value="/signin/twitter"/>"
			method="POST">
			<button type="submit">
				<img
					src="<c:url value="/resources/social/twitter/sign-in-with-twitter.png"/>" />
			</button>
		</form>
		<!-- FACEBOOK SIGNIN -->
		<form name="fb_signin" id="fb_signin"
			action="<c:url value="/signin/facebook"/>" method="POST">
			<input type="hidden" name="scope"
				value="publish_stream,user_photos,offline_access" />
			<button type="submit">
				<img
					src="<c:url value="/resources/social/facebook/sign-in-with-facebook.png"/>" />
			</button>
		</form>
	</div>
</body>
</html>