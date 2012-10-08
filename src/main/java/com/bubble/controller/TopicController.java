package com.bubble.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bubble.classifier.Classifier;
import com.bubble.db.account.Account;
import com.bubble.db.account.JdbcAccountRepository;
import com.bubble.db.comment.Comment;
import com.bubble.db.comment.CommentTree;
import com.bubble.db.comment.JdbcCommentRepository;
import com.bubble.db.subscription.SubsRepository;
import com.bubble.db.topic.Topic;

import com.bubble.db.topic.TopicRepository;
import com.bubble.db.vote.JdbcVoteRepository;
import com.bubble.db.vote.UserAlreadyVotedException;
import com.bubble.db.vote.Vote;
import com.bubble.db.comment.ArgumentGame;


@Controller
public class TopicController {

	private final Logger logger = LoggerFactory.getLogger(TopicController.class);

	private final JdbcAccountRepository accountRepo;
	private final JdbcVoteRepository voteRepo;
	private final JdbcCommentRepository commentRepo;
	private final SubsRepository subsRepo;
	private final TopicRepository topicRepo;
	private final Twitter twitter;
	private final Classifier classifier;

	@Inject
	public TopicController(Twitter twitter, 
			JdbcAccountRepository accountRepo, JdbcVoteRepository voteRepo,
			JdbcCommentRepository commentRepo, TopicRepository topicRepo, 
			SubsRepository subsRepo, Classifier classifier) {
		this.twitter = twitter;
		this.accountRepo = accountRepo;
		this.commentRepo = commentRepo;
		this.voteRepo = voteRepo;
		this.topicRepo = topicRepo;
		this.subsRepo = subsRepo;
		this.classifier = classifier;
	}

	@RequestMapping(value = "/comment/reply", method = RequestMethod.GET)
	public @ResponseBody Comment addReply(@RequestParam("topic") int topicId, 
			@RequestParam("commenter") String commenter, 
			@RequestParam("text") String text, 
			@RequestParam("parent") int parentId, 
			@RequestParam("agreeing") boolean agreeing, 
			@RequestParam("tweetMe") boolean tweetMe, 
			HttpSession session) {		
		int parentLevel = commentRepo.getLevel(parentId);
		String sentiment = determineSentiment(text);
		String photoUrl = (String) session.getAttribute("photoUrl");
		logger.debug(photoUrl);
		Account commenterAccount = accountRepo.findAccountByUsername(commenter);
		Comment newReply = new Comment(-1, topicId, commenterAccount, 
				text, parentLevel+1, parentId, new Date(), 
				photoUrl, agreeing, sentiment, 5.0);
		commentRepo.createComment(newReply);
		if (tweetMe && twitter.isAuthorized()) {
			Topic t = topicRepo.getTopic(topicId);
			String title = t.getTitle();
			title = title.replaceAll("\\s(\\s)*", "_");
			String message = "#"+title+" #Bubble ";
			int remainingSpace = 140 - message.length();
			if (text.length() < remainingSpace) {
				message += text;
			} else {
				message += text.substring(0, remainingSpace-4)+"...";
			}
			twitter.timelineOperations().updateStatus(message);
			logger.debug("Tweeted on behalf of "+commenter);
		}				
		return newReply;
	}

	@RequestMapping(value = "/comment/switchSentiment", method = RequestMethod.GET)
	public @ResponseBody Comment switchSentiment(@RequestParam("comment") int id) {
		Comment retComment = commentRepo.switchSentiment(id);
		if (retComment != null) {
			classifier.classifyTextAs(retComment.getText(), 
					retComment.getSentiment());
		}
		return retComment;
	}

	private String determineSentiment(String text) {
		return classifier.getSentiment(text).toString().toLowerCase();
	}

	@RequestMapping(value="/comment/vote", method = RequestMethod.GET)
	public @ResponseBody void vote(@RequestParam("commentId") int commentId, 
			@RequestParam("positive") boolean positive, HttpSession session) 
					throws UserAlreadyVotedException {
		voteRepo.createVote(
				new Vote(commentId, (String) session.getAttribute("uname"), 
						positive));	
	}	


	@RequestMapping(value="/follow", method = RequestMethod.GET)
	public @ResponseBody void vote(@RequestParam("topic") int topic, 
			@RequestParam("user") String user, 
			@RequestParam("subscribed") String subscribed, 
			HttpSession session){
		subsRepo.createSubscription(topic, user, 
				subscribed.trim().equals("Subscribe"));	
	}	


	@RequestMapping(value = "/topic", method = RequestMethod.GET)
	public ModelAndView topic(int id, Model model, HttpSession session) {
		Topic currentTopic = topicRepo.getTopic(id);
		model.addAttribute("currentTopic", currentTopic);
		String userName = (String) session.getAttribute("uname");
		CommentTree comments = commentRepo.getCommentsAsTree(id, userName);
		model.addAttribute("comments", comments);
		model.addAttribute("subscribed", subsRepo.isSubscribed(userName, id));
		return new ModelAndView("topic", model.asMap());
	}

	@RequestMapping(value = "/comment/calcRanks", method = RequestMethod.GET)
	public @ResponseBody Map<String, Double> calcRanks(
			@RequestParam("comment") int startComment,
			@RequestParam("topic") int topicID, @RequestParam("update") boolean positive, HttpSession session) {

		Map<Long, CommentTree> fullCommentMap = 
				commentRepo.getCommentsAsMap(topicID, 
						(String)session.getAttribute("uname"));
		Map<String, Double> out = new HashMap<String, Double>();

	
		//ArgumentGame g = new ArgumentGame(fullCommentMap);

		long currId = startComment;
		boolean increase = positive;
		double impact = 1;
		double factor = 1.2;
		while(currId != 0)
		{
			//g.setArguedComment(currId);
			//double outcome = g.play();
			CommentTree tr = fullCommentMap.get(currId);
			Comment c = tr.getComment();
			double newRank = nRank(c, increase, impact);			
			out.put("a"+currId, newRank);
			commentRepo.updateRankInDB((int) currId, newRank);
			
			currId = c.getParentId();
			if (!c.isAgreeing()) increase = !increase;			
			impact = impact/factor;
			factor = factor + 0.3;			
		} 

		fullCommentMap.clear();

		return out;
	}
	
	 double trunc(double a) {
	    	long y=	Math.round(a*10);
	        return (double) y/10;
	    }
	 
	private double nRank(Comment comment, boolean increase, double impact)
	{
		double res = comment.getRank();
		double diff = 0.5 * impact;
		
		if (increase)
			res = res + diff ;
		else
			res = res - diff;
		
		if (res > 10) res = 10;
		else if (res < 0) res = 0;
		
		return trunc(res);
	}

	@RequestMapping(value = "/topic/getSuggestions", method = RequestMethod.GET)
	public @ResponseBody String[] getSuggestions(
			@RequestParam("searchVal") String searchVal) {
		List<Topic> topics = topicRepo.getAllTopics(searchVal);
		String[] res = new String[topics.size()];
		Iterator<Topic> itr = topics.iterator();
		int i = 0;
		while(itr.hasNext()){
			res[i] = itr.next().getTitle();
			i++;
		}
		return res;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody ModelAndView search(
			@RequestParam("q") String q, Model model) {
		Topic t = topicRepo.getTopic(q);
		return new ModelAndView(
				"redirect:/topic?id="+t.getId(), model.asMap());
	}	

}
