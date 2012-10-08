<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script>

var topicTitleMaxCharacters = 25;
var commentMaxCharacters = 140;

function showRestOfForm() {
	$('.hiddenElem').css("display", "inline-block");
}

function visitTopic(bubble) {	
	var baseUrl = <c:url value="/"/>
	parent.location = baseUrl+$('a', bubble).attr('href')
}

function toTopics(order) {
	var baseUrl = <c:url value="/"/>
	parent.location = baseUrl+'/topics?order='+order;
}
function resetTopicForm() {
	$('#add_topic_textarea').val("");
	$('#add_topic_input').val("");
}

$(document).ready(function() {
	resetTopicForm();
	$('#counter').val(commentMaxCharacters - ($('#add_topic_textarea').val().length));
	$('#counter-title').val(topicTitleMaxCharacters-($('#add_topic_input').val().length));
	$('#add_topic_textarea').keydown(function(e) {
    	$('#counter').val(commentMaxCharacters - ($(this).val().length));
	});
	$('#add_topic_input').keydown(function(e) {
    	$('#counter-title').val(topicTitleMaxCharacters - ($(this).val().length));
	});
	$('#add_topic_textarea').keyup(function(e) {
    	$('#counter').val(commentMaxCharacters - ($(this).val().length));
	});
	$('#add_topic_input').keyup(function(e) {
    	$('#counter-title').val(topicTitleMaxCharacters - ($(this).val().length));
	});
});
</script>

<div style="float:left;width:31%">
<h2> Topics </h2>
<div id = "topic-sidebar" class="exploded-rounded-corners">
	<h4 class="feed_item_title"> MOST RECENT</h4>
	<table style="width:100%;table-layout:fixed">
		<col width="70%"/>
		<col width="15%"/>
		<col width="15%"/>
			<tr>
				<td></td> 
				<td title="Comments"> <span class="ui-icon ui-icon-comment"></span> </td> 
				<td title="Votes"> <span class="ui-icon ui-icon-star"></span> </td>
			</tr>
			<c:forEach items="${recentTopics}" var="topic">
			<tr> 
				<td onclick="visitTopic(this);">
					<div style="max-width:100%;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">
						<a href="topic?id=${topic.id}">${topic.title} </a>
					</div>
				</td>
			<td> ${topic.comments} </td> 
			<td> ${topic.votes} </td>
		</tr>
		</c:forEach>
		<tr> 
			<td colspan="4">
				<h4 style="padding-right:10px;curos:pointer;float:right;font-weight: 300;"> 
					<a href="#" onclick="toTopics('rec');">More...</a>
				</h4>
			</td> 
		</tr>
	</table>
	<h4> MOST POPULAR</h4>
	<table width="100%"> 
		<tr>
			<td></td> 
			<td title="Comments"> <span class="ui-icon ui-icon-comment"></span> </td> 
			<td title="Votes"> <span class="ui-icon ui-icon-star"></span> </td>
		</tr>
		<c:forEach items="${popularTopics}" var="topic">
		<tr> 
			<td onclick="visitTopic(this);"><a href="topic?id=${topic.id}">${topic.title} </a></td>
			<td> ${topic.comments} </td>
			<td> ${topic.votes} </td>
		</tr>
		</c:forEach>
		<tr> 
			<td colspan="4">
			<h4 style="padding-right:10px;curos:pointer;float:right;font-weight: 300;"> 
				<a href="#" onclick="toTopics('pop');">More...</a>
			</h4>
			</td> 
		</tr>
	</table>
</div>
</div>
<div style="width:67%; float:right">
<h2>Add a new topic</h2>
<div id="add_topic_form" class="exploded-rounded-corners">
	<form:form id="add_topic" modelAttribute="topicForm">
		<table id="add_topic_table">
			<tr>
				<td style="border-bottom: 1px dashed #000;">
					<form:input path="title" width="100%" height="100%" id="add_topic_input" maxlength="25" class="topic-input" onclick="showRestOfForm()" type="text" value="" placeholder="Topic title:"/>	
				</td>
				<td style="text-align:right">
					<input id="counter-title" class="counter hiddenElem" disabled="disabled" value="25" class="hiddenElem">
				</td>
			</tr>
			<tr>
				<td>
					<form:textarea path="initialComment" id="add_topic_textarea" maxlength="140" class="topic-input" rows="3" onclick="showRestOfForm();" placeholder="Initial Argument:" style="overflow:hidden"></form:textarea> 
				</td>
				<td style="text-align:right">
					<input id="counter" disabled="disabled" class="hiddenElem counter" value="140">
				</td>
			</tr>
		</table>
	</form:form>
</div>
<div id="add_topic_button_subcontainer" class="hiddenElem">
	<a id="add_topic_button" href="#" onclick="document.forms['add_topic'].submit();resetTopicForm();">
		<span id="add_topic_icon">Add Topic</span>
	</a>
</div>
<br/>	
<h2 id="notif-title"> Notifications </h2>
<div id="notif-sidebar" class="exploded-rounded-corners">
	<c:if test="${fn:length(notifs)==0}">
		<h4> WELCOME, ${uname}!</h4>
		<p>This is where you will get your notifications.</p>
		<p>It's looking mighty bare right now. When you comment or vote on topics the following activity on that topic will be notified here. </p>
		<p>You can check out the newest or the most popular topics on the box on the left.<p/>
		<p>You can also add a new topic on the box above. <p/>
	</c:if>
	<c:forEach items="${notifs}" var="notif" varStatus="status">
		<div class="feed_item" id="feeditem-${status.index}">
			<table style="width:100%;table-layout:fixed">
				<col width="48px"/>
				<col/>
				<tr>
					<td>
						<div class="feed_item_photo">
							<a href="${notif.photoUrl}" class="author-avatar" id="feeduserlink-${status.index}">
								<img id="feedimage-${status.index}" src="<c:url value="${notif.photoUrl}"/>" />
							</a>
						</div>
					</td>
					<td>
						<div style="width:100%">
							<strong class="feed_item_title">
								<span class="feed_item_user">
									<a href="" id="feeduser-${status.index}">${notif.user}</a> 
								</span>
								<span class="action" id="${notif.notifId}:${notif.topicId}"> 
									<span id="feedaction-${status.index}"><% String action = "commented"; %>
										<c:if test="${notif.notifId == -1}"><%action = "voted on a comment"; %></c:if><%=action %>
									</span> 
									on the 
									<span onclick="visitTopic(this);">
										<a href="topic?id=${notif.topicId}" id="feedtopic-${status.index}">${notif.topicTitle} </a>
									</span> 
									topic 
								</span>
							</strong>
							<div style="word-wrap: break-word;" id="feedcomment-${status.index}">
							<c:if test="${notif.notifId == -1}">
								<a href="#">${notif.commenter}</a>: 
							</c:if>
							${notif.comment}
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</c:forEach>
</div>
</div>
