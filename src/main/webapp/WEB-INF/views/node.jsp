<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<li>
	<div class="comment-block" id="${node.comment.id}"
		style="border-left-color:
		<c:if test="${node.comment.agreeing}">
			#A9C9A4
		</c:if>
		<c:if test="${!node.comment.agreeing}">
		#FF6666
		</c:if>
" />
	<a href="${node.comment.photoUrl}" class="author-avatar"> <img
		src="<c:url value="${node.comment.photoUrl}"/>" />
</a>
	<p class="comment-top-meta">
		<a href="#author${node.comment.id}" id="author">${node.comment.commenter.username}</a> <span class="replies"
			id="replies${node.comment.id}"> <c:if
				test="${fn:length(node.replies) > 0}">
            		${fn:length(node.replies)} replies
            	</c:if>
		</span>
		<!-- Display for the moment posvotes+negVotes as the rank. This will be
            	replaced once the semantic analysis is integrated  -->
		<span class="rank" title="Comments with a high rank are those which are supported by other people the most. " id="rank${node.comment.id}"> Rank:${node.comment.rank}</span> <abbr class="timeago"
			title="${node.comment.isoDateCreated}"></abbr>
	</p>
	<div class="comment-content"><p>
	
		<div class="the-comment-post"> <span class="replies">${node.comment.text}</span> 
			(Bubble thinks this is: <span class="sentiment" id="sentiment${node.comment.id}" title="Click if you think this sentiment is wrong" onclick="wrongSentiment(${node.comment.id})">${node.comment.sentiment}</span>)</div></p>
			
		<p class="comment-reply-options" id="reply${node.comment.id}">
			<a class="option-reply" id="option-reply${node.comment.id}">Reply</a>
			<span class="bull">&bull;</span>
			<c:if test="${node.comment.userVote == 0}">
				<a class="option-vote" onclick="vote(${node.comment.id},${true})">
					Upvote </a>
				<span class="bull">&bull;</span>
				<a class="option-vote" onclick="vote(${node.comment.id},${false})">
					Downvote </a>
			</c:if>
			<c:if test="${node.comment.userVote == -1}">
				<span class="voted"> Downvoted</span>
			</c:if>
			<c:if test="${node.comment.userVote == 1}">
				<span class="voted"> Upvoted</span>
			</c:if>
		</p>
		<span class="showhidecommform">&nbsp;</span>

	</div>
	<input type="hidden" id="age" value="${node.comment.commenter.age}"/>
	<input type="hidden" id="sentiment" value="${node.comment.sentiment}"/>
	</div>

	<ul class="thread-replies">
		<c:forEach items="${node.replies}" var="comment" varStatus="loopCount">
			<c:set var="node" value="${comment}" scope="request" />
			<jsp:include page="node.jsp" />
		</c:forEach>
	</ul>
</li>
