<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	
	<script>
	
	function calculateRank() {
		var totalRank = 0;
		var totalComments = 0;
		var posRank = 0;
		$('.comment-block').each(function() {
			var sentiment = $('#sentiment', this).val();
			var rank = parseRank($($('.rank' ,this)[0]).html());
			totalRank += parseInt(rank);
			totalComments++;
  		  	if(sentiment == "positive") {
  		  		posRank += parseInt(rank);
  		  	} 
		});
		var overallRank = (posRank * 100) / totalRank;
		if(totalComments < 5) {
			$("#overall-rank").html("0%");
			$("#score").val(0);
		} else {
			$("#overall-rank").html(Math.round(overallRank) + "%");
			$("#score").val(Math.round(overallRank));
			if(overallRank >= 50) {
				$("#overall-rank").css("color", "#75ee6d");
			} else {
				$("#overall-rank").css("color", "#e16f6b");
			}
		}
	}
	
	function toggleSubscribe() {
		var topicId = document.getElementById("topicId").value;
		var user = document.getElementById("commenter").value;
		var subscribed = $( "#subscribe" ).button( "option", "label" );
		$.getJSON(
				"follow",
				{
					topic : topicId,
					user : user,
					subscribed:subscribed,
				},
				function() {
					if(subscribed.trim() == "Subscribe") 
						pressSubscribe();
					else 
						pressUnsubscribe();				
					}
				);
	}
	
		function pad(num) {
			return ("0" + num).slice(-2);
		}
		
		function addComment(parentId) {
			var txt = $('textarea#thecomment' + parentId).val();

			if (txt == "") {
				{
					alert("Cannot create an empty comment!");
				}
				return false;
			}

			var agreeing = $(
					'input:radio[name="agreeing' + parentId + '"]')[0]
					.checked;
			var topicId = document.getElementById("topicId").value;
			var commenterName = document.getElementById("commenter").value;
			var tweetMe = $('input:checkbox[name="tweetMe'+parentId+'"]').attr('checked');
			$
					.getJSON(
							"comment/reply",
							{
								topic : topicId,
								commenter : commenterName,
								text : txt,
								parent : parentId,
								agreeing : agreeing,
								tweetMe : tweetMe
							},
							function(comment) {
								var photoUrl = document.getElementById("photoUrl").value;
								var borderColor = agreeing ? "#A9C9A4" : "#FF6666";

								var newcommhtml = '<div class="comment-block"  id="'+comment.id+'" style="border-left-color:' + borderColor + '">'
										+ '<a href="#" class="author-avatar">'
										+ '<img src="<c:url value="' + photoUrl + '"/>"/></a>'
										+ '<p class="comment-top-meta"><a href="#">'
										+ commenterName
										+ '</a>'
										+ '<span class="replies" id="replies'+ comment.id + '"></span>'
										+ '<span class="rank" id="rank'+comment.id+'"> Rank: '+ comment.rank + '.0 </span>'
										+ '<abbr class="timeago">less than a minute ago</abbr>'
										+ '<div class="comment-content"><p class="the-comment-post">'
										+ txt
										+ ' (Bubble thinks this is: <span class="sentiment" id="sentiment'+comment.id+'" title="Click if you think this sentiment is wrong" onclick="wrongSentiment('+comment.id+')">'+comment.sentiment+'</span>)'
										+ '</p><p class="comment-reply-options" id="reply'+comment.id+'">'
										+ '<a class="option-reply" id="option-reply'+comment.id+'">Reply</a><span class="bull">&bull;</span>'
										+ '<a class="option-vote" onclick="vote('
										+ comment.id
										+ ', '
										+ true
										+ ')"> Upvote </a><span class="bull">&bull;</span>'
										+ '<a class="option-vote" onclick="vote('
										+ comment.id
										+ ', '
										+ false
										+ ')"> Downvote </a>'
										+ '</p> <div class="showhidecommform">&nbsp;</div></div></div>'
										+ '<ul class="thread-replies"></ul>';
								$("#" + parentId).next(".thread-replies")
										.hide().append(newcommhtml).fadeIn();
								$("#option-reply" + parentId).trigger("click");
								updateParentReplies(parentId);
								recalcRanks(parentId);
								pressSubscribe();
								calculateRank();
							});
						
		}

		function updateParentReplies(parentId) {
			var repliesNo = parseInt($("#replies" + parentId).html());
			var newRepliesNo;
			if (!isNaN(repliesNo)) {
				newRepliesNo = repliesNo + 1;
			} else {
				newRepliesNo = 1;
			}
			document.getElementById("replies" + parentId).innerHTML = newRepliesNo
					+ " replies";

		}
		
		function wrongSentiment(id) {
			$.getJSON(
					"comment/switchSentiment",
					{
						comment : id
					},
					function(comment) {
						document.getElementById("sentiment" + id).innerHTML = comment.sentiment;
						filter();
					});
		}

		function vote(id, positive) {
			$
					.getJSON(
							"comment/vote",
							{
								commentId : id,
								positive : positive
							},
							function() {
								var replyElement = document
										.getElementById("reply" + id);
								var voteElements = replyElement
										.getElementsByTagName("a");
								voteElements[1].style.display = 'none';
								voteElements[2].style.display = 'none';
								replyElement.getElementsByClassName("bull")[0].style.display = 'none';
								var confirmVote = document
										.createElement("span");
								confirmVote.className = "voted";
								confirmVote.innerHTML = positive ? "Upvoted"
										: "Downvoted";
								replyElement.appendChild(confirmVote);
								
								recalcRanks(id);
								pressSubscribe();
								calculateRank();
							});			
		}
		
		function recalcRanks(id) {
						
			var topicId = document.getElementById("topicId").value;
			
			$.getJSON(
					"comment/calcRanks",  
					{ comment : id, 
					  topic : topicId
					},
					function(map) {
						$.each(map, function(i, val){
							id = parseInt(i.substring(1));
							rankElem = document.getElementById('rank'+id);
							if(isInt(val)) val = val.toString() + ".0";
							rankElem.innerHTML = "Rank: "+ val;
						});
						calculateRank();
					});		
		}
		
		function isInt(value){ 
			  if((parseFloat(value) == parseInt(value)) && !isNaN(value)){
			      return true;
			  } else { 
			      return false;
			  } 
			}

</script>

<div id="topic-content">
<h1 id="topic-title">${currentTopic.title}: <span id="overall-rank"></span></h1>
	<div style="text-align:center">
	<input type="checkbox" id="subscribe" onclick="toggleSubscribe()"/>
	<label for="subscribe" id="subscribe-label" style="font-size:70%;"><%String subsAction="Subscribe";%>
	<c:if test="${subscribed}"><%subsAction="Unsubscribe";%></c:if><%=subsAction%></label>
	</div>
	<svg id="main" width="800" height="200" xmlns="http://www.w3.org/2000/svg" version="1.1">
	<defs>
		<linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="0%">
			<stop offset="0%" style="stop-color:#75ee6d;stop-opacity:1" />
			<stop offset="100%" style="stop-color:#e16f6b;stop-opacity:1" />
		</linearGradient>
		<marker id="TriangleNeg" 
			stroke="#e16f6b"
			fill="#e16f6b"
			viewBox="0 0 10 10" refX="0" refY="5" 
			markerUnits="strokeWidth"
			markerWidth="4" 
			markerHeight="3" 
			orient="auto">
      	<path d="M 0 0 L 10 5 L 0 10 z" />
		<marker id="TrianglePos"
			stroke="#75ee6d"
			fill="#75ee6d"
			viewBox="0 0 10 10" refX="0" refY="5" 
			markerUnits="strokeWidth"
			markerWidth="4" markerHeight="3"
			orient="auto">
		<path d="M 0 0 L 10 5 L 0 10 z" />
		<marker id="TriangleRank"
			stroke="silver"	
			fill="silver"
			viewBox="0 0 10 10" refX="0" refY="5" 
			markerUnits="strokeWidth"
			markerWidth="3" markerHeight="3"
			orient="auto">
		<path d="M 0 0 L 10 5 L 0 10 z" />
	</defs> 
	<polygon style="stroke-width:0.03%;stroke:black" fill="url(#grad)" />
	<rect style="stroke-width:0.03%;stroke:black" fill="url(#grad)" />
	<path id="sent-neg-arrow" fill="none" stroke="#e16f6b" stroke-width="4" marker-end="url(#TriangleNeg)" />
	<path id="sent-pos-arrow" fill="none" stroke="#75ee6d"stroke-width="4" marker-end="url(#TrianglePos)" />
	<text id="sent-neg-desc" fill="#e16f6b">Negative</text>
	<text id="sent-pos-desc" fill="#75ee6d">Positive</text>     
	<text id="sent-desc" fill="url(#grad)">Sentiment</text>     
	<path id="rank-low-arrow" fill="none" stroke="silver" stroke-width="1.5" marker-end="url(#TriangleRank)" />
	<path id="rank-height-arrow" fill="none" stroke="silver"stroke-width="1.5" marker-end="url(#TriangleRank)" />
	<text id="rank-low-desc" fill="silver" font-size="10px">Low</text>
	<text id="rank-high-desc" fill="silver" font-size="10px">High</text>     
	<text id="rank-desc" fill="silver" font-size="10px">Rank</text>     
</svg> 
<form id="ageradio">
		<input type="radio" id="all" value="all" class="ageradio" name="radio" checked="checked"/><label for="all">All ages</label>
		<input type="radio" id="age1" value="age1" class="ageradio" name="radio"/><label for="age1">Age 18-29</label>
		<input type="radio" id="age2" value="age2" class="ageradio" name="radio"/><label for="age2">Age 30-49</label>
	    <input type="radio" id="age3" value="age3" class="ageradio" name="radio"/><label for="age3">Age 50-65</label>
	    <input type="radio" id="age4" value="age4" class="ageradio" name="radio"/><label for="age4">Age 65+ </label>
</form>
<jsp:include page="visualization.jsp" />
<script> 
function pressUnsubscribe() {
	$("#subscribe-label").removeClass('ui-state-active');
	$("#subscribe-label").attr("aria-pressed", "false");
	$( "#subscribe" ).button( "option", "label", "Subscribe");
}

function pressSubscribe() {
	$("#subscribe-label").addClass('ui-state-active');
	$("#subscribe-label").attr("aria-pressed", "true");
	$( "#subscribe" ).button( "option", "label", "Unsubscribe");
}

	$(document).ready(function(){
		calculateRank();
		$("#ageradio").buttonset().change(function() {change_age($('.ageradio:checked').val()); });
		$(".button").button();
		$( "#subscribe" ).button();
		if($( "#subscribe" ).button( "option", "label" ).trim() == "Subscribe"){
			pressUnsubscribe();
		} else {
			pressSubscribe();
		}
	});
</script>

<input type="hidden" id="topicId" value="${currentTopic.id}" />
<!-- <input type="hidden" id="commenter" value="${profile.screenName}" />
<input type="hidden" id="photoUrl" value="${profile.profileImageUrl}" /> -->

<input type="hidden" id="commenter" value="${uname}" />
<input type="hidden" id="photoUrl" value="${photoUrl}" />

<c:set var="topicId" value="${currentTopic.id}" scope="request" />
<div id="content">
	<ul id="comments" class="comment-thread">
		<c:forEach items="${comments.replies}" var="comment">
			<c:set var="node" value="${comment}" scope="request" />
			<jsp:include page="node.jsp" />
		</c:forEach>
	</ul>
</div>
<div style='text-align:center'>
<form:form modelAttribute="bubblegraphics" action="bubblegraphics" method="GET">
	<input type="hidden" value="${currentTopic.id}" name="topic"/>
	<input type="hidden" name="score" id="score"/>
	<input class="button" type="submit" style="font-size:70%" value="To Bubble Graphics!" />
</form:form>
</div>
</div>
