$.fn.collapsible = function() {
	
	$('li', '#content').each(function() {
		if($('>ul, >ol',this).size() > 0){
			$('>ul, >ol',this).hide(); 
		}
	});
	
	$(".replies").hover(function() {
		$(this).css("cursor", "pointer");
	});

	$(".replies").click(function() {
		var parent = $(this).closest('li');
		$('>ul, >ol',parent).slideToggle('medium');
	});
};

$.fn.uncollapse = function() {
	var parent = $(this).closest('ul');
	var grandparent = $(parent).closest('li');
	if($('.comment-block', grandparent).attr('id') == $(this).attr('id')) {
		return;
	}
	$('>ul, >ol',grandparent).show();
	$('.comment-block', grandparent).uncollapse();
};
