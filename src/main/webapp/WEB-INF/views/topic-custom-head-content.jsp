<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type='text/javascript'> 
var AJAX_FAILED = -1;
$(document).ready(function(){ $('#comments').collapsible();});
$(document).ready(function() {$("abbr.timeago").timeago();});
</script>
<script type="text/javascript" src="<c:url value="/resources/scripts/jCollapsible.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/scripts/jReply.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/scripts/jTimeAgo.js"/>"></script>
<link rel="stylesheet" type="text/css" media="all" href="<c:url value="/resources/styles/style.css"/>"/>
<link rel="stylesheet" href="<c:url value="/resources/jqueryui/css/jquery-ui-1.8.16.custom.css"/>" />
<script src="<c:url value="/resources/jqueryui/jquery.ui.core.min.js"/>"></script>
<script src="<c:url value="/resources/jqueryui/jquery.ui.widget.min.js"/>"></script>
<script src="<c:url value="/resources/jqueryui/jquery.ui.button.min.js"/>"></script>