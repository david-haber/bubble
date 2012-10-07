<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<script>
	   var filteredIcons = new Array();
	   var allIcons = new Array();
	   var age = "all";
	   var measures;
	   var comments = new Array();
	   var selectedId = null;
	   
	   window.onload=function() {
	     measures =  new initMeasures();
	     drawPlatform();
	     initComments();
	     /* populate the comments array with mock data. 
	        to be removed when we connect to the db */
	     for(var c in comments) {
	       allIcons[c] = new icon(comments[c]);
	       drawPpl(allIcons[c]);
	     }
	     filteredIcons = allIcons;
	     change_age($('.ageradio:checked').val()); 
	   };
	   
	function apply_age_filter(age, ppl) {
	  switch(age) {
	    case "age1": return filter_age(18, 29, ppl);
	    case "age2": return filter_age(30, 49, ppl);
	    case "age3": return filter_age(50, 65, ppl);
	    case "age4": return filter_age(65, 120, ppl);
	    default: return ppl; 
	  }

	  function filter_age(min, max, ppl) {
	    var newPplIcons = new Array();
	    var next = 0;
	    for(var c in ppl) {
	      var age = ppl[c].comment.person.age;
	      if(age >= min && age <= max) {
	        newPplIcons[next] = ppl[c]; 
	        next++;
	      }
	    }
	    return newPplIcons;
	  }
	}


	function change_age(ageVal) {
	  age = ageVal;
	  filter();
	}

	function filter() {
	  var newFilteredIcons = apply_age_filter(age, allIcons);
	  transition(newFilteredIcons);
	  filteredIcons = newFilteredIcons;
	  $('.people').each(function() {
	    $(this).click(function(){
		  var jump = $(this).attr('id').split(":")[1];
		  if (selectedId != null) {
		    $('#' + selectedId).css('background-color', 'white');
		  }
		  selectedId = jump;
		  $('#' + jump).css('background-color', 'GhostWhite');
		  $('#' + jump).uncollapse();
		  var new_position = $('#'+jump).offset();
		  $('html, body').animate({ 
		    scrollTop: new_position.top
		    }, 1000);
		  return false;
		});
	  });
	}

	function transition (newFilteredIcons) {
	  var parent = document.getElementById("main");
	  for(var c = newFilteredIcons.length; c < filteredIcons.length; c++) {
	     var child = document.getElementById("image:" + filteredIcons[c].comment.id);
	     parent.removeChild(child);
	  }
	  
	  for(var c = filteredIcons.length; c < newFilteredIcons.length; c++) {
	    var comm = new comment(
	        new person("", Math.floor(Math.random()*90)), 
	        "text"+c, 
	        Math.floor(Math.random()*11), 
	        Math.floor(Math.random()*3),
	        Math.random());
	    filteredIcons[c] = new icon(comm);
	    drawPpl(filteredIcons[c]);
	  }
	  
	  for(var c = 0; c < newFilteredIcons.length; c++) {
	    var iconn = document.getElementById("image:" + filteredIcons[c].comment.id);
	    move(iconn, 'x', filteredIcons[c].pos[0], newFilteredIcons[c].pos[0]);      
	    move(iconn, 'y', filteredIcons[c].pos[1], newFilteredIcons[c].pos[1]);      
	  }
	  changeIcons(filteredIcons, newFilteredIcons);  
	 }

	function changeIcons(oldIcons, newIcons) {
	  for(var i in newIcons) {
	    var icon = document.getElementById("image:" + oldIcons[i].comment.id);
	    icon.setAttribute('id', 'image:' + newIcons[i].comment.id);
	  }
	}

	function move(icon, direction, from, to) {
	  if (Math.abs(from - to) > 1) {
		if(from < to) {
	      from += 1;
	    } else {
	      from -= 1;
	    }
	    icon.setAttributeNS(null, direction, from);
	    window.setTimeout(function(){move(icon, direction,from,to);}, 1);
	  }
	}

	function drawPpl(ppl) {
	  var img = document.createElementNS("http://www.w3.org/2000/svg","image");
	  img.setAttributeNS("http://www.w3.org/1999/xlink","href", 'resources/images/ppl1.png'); 
	  img.setAttribute('x', ppl.pos[0]);
	  img.setAttribute('y', ppl.pos[1]);
	  img.setAttribute('width', 20);
	  img.setAttribute('height', 40);
	  img.setAttribute('id', 'image:' + ppl.comment.id);
	  img.setAttribute('class', 'people');
	  img.setAttribute('title', ppl.comment.person.username);
	  document.getElementById("main").appendChild(img);
	}

	function initMeasures() {
	  this.height = 100;
	  this.topp = 40;
	  this.left = 100;
	  this.variance = 40;
	  this.width = 600;
	  this.depth = 10;
	  this.pplmargin = 20;
	}

	function parseRank(rank) {
		var r = rank.split(":");
		if(r.length != 2) {
			return 7; //return the default rank
		} 
		return r[1];
	}
	
	function initComments() {
		var c = 0;
		$('.comment-block').each(function() {
			var username = $('#author',this).html();
			var age = $('#age', this).val();
			var sentiment = $('#sentiment', this).val();
			var text = $($('.the-comment-post' ,this)[0]).html();
			var rank = parseRank($($('.rank' ,this)[0]).html());
			var id = $(this).attr('id');
  		  var sentCode;
  		  if(sentiment == "positive") {
  			  sentCode = 0;
  		  } else if(sentiment == "neutral") {
  			  sentCode = 1;
  		  } else {
  			  sentCode = 2;
  		  }
	      var comm = new comment(new person(username, age), text, rank, sentCode, id);
	      comments[c] = comm;
	      c++;
		});
	}

	function drawPlatform() {
	  var polygon = document.getElementsByTagName("polygon")[0];
	  var points = 
	    measures.left + ',' + measures.topp + ' ' + 
	    (measures.left + measures.width) + ',' + measures.topp + ' ' + 
	    (measures.left + measures.width + measures.variance) + ',' + (measures.topp + measures.height) + ' ' + 
	    (measures.left - measures.variance) + ',' + (measures.topp + measures.height);  
	  polygon.setAttribute('points', points);  
	  var rect = document.getElementsByTagName("rect")[0];
	  rect.setAttribute('x', measures.left-measures.variance); 
	  rect.setAttribute('y', measures.topp+measures.height); 
	  rect.setAttribute('width', measures.width + 2 * measures.variance); 
	  rect.setAttribute('height', measures.depth);
      var negArrow = document.getElementById("sent-neg-arrow");
      negArrow.setAttribute('d', 
        'M '+ (measures.left + measures.width + measures.variance - 30) + ' ' +
			  (measures.topp + measures.height + measures.depth + 10) + 
		' L '+ (measures.left + measures.width + measures.variance - 10) + ' ' +
	           (measures.topp + measures.height + measures.depth + 10));
      var posArrow = document.getElementById("sent-pos-arrow");
      posArrow.setAttribute('d', 
        'M' + (measures.left - measures.variance + 30) + ' ' + 
              (measures.topp + measures.height + measures.depth + 10) + 
        ' L ' + (measures.left - measures.variance + 10) + ' ' +
                (measures.topp + measures.height + measures.depth + 10));
      var sentNeg = document.getElementById("sent-neg-desc");
      var sentPos = document.getElementById("sent-pos-desc");
      var sent = document.getElementById("sent-desc");
      sentNeg.setAttribute('x', (measures.left + measures.width + measures.variance-110));
      sentNeg.setAttribute('y', (measures.topp + measures.height+measures.depth+14));
	  sentPos.setAttribute('x', (measures.left-measures.variance+35));
      sentPos.setAttribute('y', (measures.topp + measures.height+measures.depth+14));
      sent.setAttribute('x', ((measures.left + measures.width + measures.variance-50)/2));
      sent.setAttribute('y', (measures.topp + measures.height+measures.depth+14));
      var rankLow = document.getElementById("rank-low-desc");
      var rankhigh = document.getElementById("rank-high-desc");
      var rankDesc = document.getElementById("rank-desc");
      var lowArrow = document.getElementById("rank-low-arrow");
      var heightArrow = document.getElementById("rank-height-arrow");
      rankLow.setAttribute('x', measures.left);
      rankLow.setAttribute('y', measures.topp+5);
      rankLow.setAttribute('transform', 'rotate(-65 110, 68)');
      rankhigh.setAttribute('x', (measures.left -measures.variance));
      rankhigh.setAttribute('y', (measures.topp + measures.height));
      rankhigh.setAttribute('transform', 'rotate(-65 50, 135)');
      rankDesc.setAttribute('x', (measures.left*2-measures.variance)/2);
      rankDesc.setAttribute('y', (measures.topp+measures.height/2)+5);
      rankDesc.setAttribute('transform', 'rotate(-65 78, 105)');
      lowArrow.setAttribute('d', 
        'M ' + (measures.left-8) + ' ' + (measures.topp + 7) + 
        ' L ' + (measures.left-5) + ' ' + (measures.topp));
      heightArrow.setAttribute('d', 
        'M ' + (measures.left - measures.variance-6) + ' ' + 
          (measures.topp + measures.height-7) + 
        ' L ' + (measures.left - measures.variance -9)+ ' ' +
          (measures.topp + measures.height)); 
  }

	function comment(person, text, rank, agreeing, id) {
	  this.person = person;
	  this.text = text;
	  this.rank = rank;
	  this.agreeing = agreeing;
	  this.id = id;
	}

	function person(username, age) {
	  this.username = username;
	  this.age = age;
	}

	function icon(comment) {
	  this.comment = comment;
	  this.pos = getPosition(comment, measures);
	  function getPosition(comment, measures) {
	    var rank = comment.rank;
	    var x = (rank/10) * (measures.height - measures.pplmargin) + measures.topp - 40 + measures.pplmargin/2;
	    var y;
	    var widthNeutr = (measures.width + measures.variance) / 3;
	    var widthPosNeg = (measures.width - widthNeutr) / 2;
	    if(comment.agreeing == 1) {
	      y = Math.random()*widthNeutr + widthPosNeg + measures.left;
	    } else {
	      var variance = (x * measures.variance)/measures.height;
	      var yAbsolut = Math.random()*(variance + widthPosNeg);
	      if (comment.agreeing == 0) {
	        y = measures.left - variance + yAbsolut + measures.pplmargin; 
	      } else {
	        y = measures.left + widthPosNeg + widthNeutr + yAbsolut - measures.pplmargin;
	      }
	    }
	    return new Array(Math.floor(y),x);
	  }
	}
</script>