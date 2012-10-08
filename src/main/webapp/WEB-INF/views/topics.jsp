<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script>
//Green, Grey, Blue, Cream, Red
var fillColors = ["#129793","#505050","#9BD7D5","#FFF5C3", "#FF7260"];
var antiColors = ["#FFF5C3","#9BD7D5", "#FF7260", "#505050", "#129793"];

function draw() {
	$('.bubble').each(function(index) {
		if (index < topicsList.length) {
		var ctx = $(this).find('canvas')[0].getContext("2d");
		//draw a circle
		var oldStyle = ctx.strokeStyle;
		var oldFillStyle = ctx.fillStyle;
		ctx.strokeStyle = fillColors[index % 5];
		ctx.fillStyle = fillColors[index % 5];
		ctx.beginPath();
		ctx.arc(90, 90, 90, 0, Math.PI*2, true);
		ctx.closePath();
		ctx.fill();
		ctx.strokeStyle = oldStyle;
		ctx.fillStyle = oldFillStyle;
		}
	});
}

function hideBubbles() {
	$(".bubble").each(function(index) {
		$(this).css("opacity","0.0");
	});
	$(".bubble-text").each(function(index) {
		$(this).css("opacity","0.0");
	});
}

function rollin(index) {
	if (index < 11) {
		var bubble = $(".bubble:eq("+index+")");
		$(bubble).animate({opacity : 1.0},'750', function() {
			$(".bubble-text:eq("+index+")").animate({opacity : 1.0},'750');
			rollin(index+1);
		});
	}
}

var currentFirstTopic = -1;


function changeTopics() {

	$(".bubble-text").fadeOut('fast', function () {
		setTopics();
		$(".bubble-text").fadeIn('fast');
	});
}

function setTopics() {
	var from = currentFirstTopic;
	var spaces = 11;
	var overlayCells = $('#circles-overlay').find('td');
	var list = topicsList;
	var nextTopic;
	$('.bubble-text > a').each(function(index) {
		currentFirstTopic = (currentFirstTopic + 1) % list.length;
		var topic = list[currentFirstTopic];
		if (topic) {
			$(this).css("color", antiColors[index % antiColors.length] );
			$(this).attr("href", "topic?id="+topic[0]);
			$(this).html(topic[1]);
		}
	});
}

function showRestOfForm() {
	$('.hiddenElem').css("visibility", "visible");
}

function visitTopic(bubble) {
	
	var baseUrl = <c:url value="/"/>
	parent.location = baseUrl+$('a', bubble).attr('href')
}

//Each element is [id,name]
var topicsList = new Array();
<c:forEach items="${topics}" var="topic" varStatus="loopCount">
<c:set var="topicTitle" value="${topic.title}"/>
<c:if test="${fn:length(topicTitle) > 12}">
<c:set var="topicTitle" value="${fn:substring(topicTitle, 0, 10)}.."/>
</c:if>
topicsList[${loopCount.count-1}] = new Array();
topicsList[${loopCount.count-1}][0]="${topic.id}";topicsList[${loopCount.count-1}][1]="${topicTitle}";
</c:forEach> 

</script>

Join an existing conversation

<div id="choose-a-topic">
	<div id="circles">
		<table>
			<tr>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
			</tr>
			<tr>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
			</tr>
			<tr>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
				<td class="bubble"><canvas width="180" height="180"></canvas></td>
			</tr>
		</table>
	</div>

	<div id="circles-overlay">
		<table style="border-style: solid; border: medium;">
			<tr>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
			</tr>
			<tr>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
			</tr>
			<tr>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
				<td class="bubble-text" onclick="visitTopic(this);"><a href=""></a></td>
				<td><a onclick="changeTopics()">More</a></td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript"> draw(); hideBubbles(); setTopics(); rollin(0); </script>

