<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="imgUrl"
	value="<%= request.getSession().getValue(\"photoUrl\") %>" />
<c:set var="profileUrl"
	value="<%= request.getSession().getValue(\"twitterUrl\") %>" />
<c:set var="uname"
	value="<%= request.getSession().getValue(\"uname\") %>" />
<c:set var="votes"
	value="<%= request.getSession().getValue(\"votes\") %>" />
<c:set var="comments"
	value="<%= request.getSession().getValue(\"comments\") %>" />
<c:set var="rating"
	value="<%= request.getSession().getValue(\"rank\") %>" />
<c:set var="logo">
	<c:url value="/resources/images/BubbleLogo.png" />
</c:set>
<script src="<c:url value="/resources/jqueryui/jquery.ui.core.min.js"/>"></script>
<script src="<c:url value="/resources/jqueryui/jquery.ui.widget.min.js"/>"></script>
<script src="<c:url value="/resources/jqueryui/jquery.ui.position.js"/>"></script>
<script src="<c:url value="/resources/jqueryui/jquery.ui.autocomplete.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/resources/jqueryui/css/jquery-ui-1.8.16.custom.css"/>" />

<script>
	function showUserMenu() {}
	$(function() {		
		$( "#search-query" ).autocomplete({	
			autoFocus: true,
			select: function(event, ui) { 
				$("#search-query").val(ui.item.value);
				document.forms["search-form"].submit();},
			source: function(req, add){
				$.getJSON(
						"topic/getSuggestions",  
						{ 
						  searchVal : req.term						  
						},
						function(data) {
		                    //create array for response objects  
		                    var suggestions = [];		  
		                    $.each(data, function(i, val) {  
		     	               suggestions.push(val.name);  
		 	                });  		  
		                	//pass array to callback  
		                	add(data);
						});	
        	}
		});
	});
</script>
<div id="top-bar-outer">
	<div id="top-bar">
		<div class="top-bar-inside" style="left: 0px;">
			<div class="static-links">
				<div ><a href='<c:url value="/"/>'><img id="logo-img"src="${logo}" /></a></div>			
			</div>

			<div class="active-links">
				<form id="search-form" onsubmit="return false;" name="search-form" class="ui-widget" method="GET" action="/Bubble/search">
					<input id="search-query" type="text" name="q" placeholder="Search" value="">
				</form>				
				<ul id="session">
					<li><a href="${profileUrl}" target="_blank"><img id="user-img" src="${imgUrl}" /></a>
					<span id="screen-name"> <a href="">${uname}</a> </span>
					<span id="screen-name-dropdown"> 
						<a href='<c:url value="/"/>'>
						<!-- img src="<c:url value='/resources/images/arrow_down.png'/>"/--></a>
					</span>
						<ul id="user-dropdown">
                    		<li>
                        		<img class="corner_inset_left" alt="" src="<c:url value='/resources/images/corner_inset_left.png'/>"/>
                        		Votes: ${votes}<br/>Comments: ${comments}<br/>Rating: ${rating}
                        		<img class="corner_inset_right" alt="" src="<c:url value='/resources/images/corner_inset_right.png'/>"/>
                    		</li>
                    		<li><a href='<c:url value="/signout"/>'>Log out</a></li>
                    		<li class="last">
                        		<img class="corner_left" alt="" src="<c:url value='/resources/images/corner_left.png'/>"/>
                        		<img class="middle" alt="" src="<c:url value='/resources/images/dot.gif'/>"/>
                        		<img class="corner_right" alt="" src="<c:url value='/resources/images/corner_right.png'/>"/>
                    		</li>
                    	</ul>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>