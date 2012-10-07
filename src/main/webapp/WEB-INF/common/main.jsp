<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><tiles:insertAttribute name="title"/></title>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/styles/main.css"/>" />
<script type="text/javascript" src="<c:url value="/resources/scripts/jquery-1.4.2.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/scripts/jquery.fullbg.min.js"/>"></script>
<tiles:insertAttribute name="custom-head-content" />
<style type="text/css">
.fullBg {
	position: fixed;
	top: 0;
	left: 0;
	overflow: hidden;
	z-index:-100;
}
</style>
<script type="text/javascript">
$(window).load(function() {
	$("#background").fullBg();
});
</script>
</head>
</head>

<!-- b5,b7, b10, b11, b13!!-->
<body>
<img src="/Bubble/resources/images/bubbles.jpg" alt="" id="background" />
<div id="container" style="position: absolute;top:0;">
<div id="header">
<tiles:insertAttribute name="header"/>
</div>
<div id="body">
<div id="main-content">
<tiles:insertAttribute name="body"/>
</div>
</div>
<div id="footer">
<tiles:insertAttribute name="footer"/>
</div>
</div>
</body>
</html>