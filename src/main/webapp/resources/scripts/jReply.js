$(document).ready(function() {
	$(".option-reply").live("click", function() {
		var currtxt = $(this).text();
		var curroptlink = $(this);
		var parentId = $(this).parent().parent().parent().attr("id");
		var htmlform = 
			'<form id="commsubform">'+
			'<span style="margin-right: 10px">'+
				'<input type="radio" name="agreeing'+parentId+'" value="true" checked> Agree </input>'	+
				'<input type="radio" name="agreeing'+parentId+'" value="false"> Disagree </input></span><br />'	+
			'<textarea id="thecomment'+parentId+'" maxlength="140" rows="5" class="commtxtfield"></textarea><br /><br /> '+
			'<a class="savecommbtn" onclick="addComment(\''+parentId+'\')">Save Comment</a> <input type="checkbox" name="tweetMe'+parentId+'" value="true"/> <img src="/Bubble/resources/social/twitter/twitter_newbird_blue.png" alt="tweetMe"/></form>';

		// toggle between show/hide comment form views
		if(currtxt == "Reply") {
			$(this).parent().next(".showhidecommform").html(htmlform);
			$(this).text("Hide form");
		}

		if(currtxt != "Reply") {
			curroptlink.parent().next(".showhidecommform").html("&nbsp;");
			curroptlink.text("Reply");
		}

		return false;
	});
});
