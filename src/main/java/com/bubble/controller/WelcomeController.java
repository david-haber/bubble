package com.bubble.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bubble.classifier.Classifier;
import com.bubble.db.account.JdbcAccountRepository;
import com.bubble.db.comment.Comment;
import com.bubble.db.comment.JdbcCommentRepository;
import com.bubble.db.notification.JdbcNotificationRepository;
import com.bubble.db.notification.Notification;
import com.bubble.db.topic.TopicAlreadyExistsException;
import com.bubble.db.topic.TopicForm;
import com.bubble.db.topic.TopicRepository;
import com.bubble.db.vote.JdbcVoteRepository;


@Controller
public class WelcomeController {
	
	private final Classifier classifier;

	private final TopicRepository topicRepo;

	private final JdbcCommentRepository commentRepo;
	
	private final JdbcAccountRepository accountRepo;

	private final JdbcVoteRepository voteRepo;
	
	private final JdbcNotificationRepository notifRepo;

	@Inject
	public WelcomeController(Classifier classifier, JdbcAccountRepository accountRepo,
			JdbcCommentRepository commentRepo, TopicRepository topicRepo, 
			JdbcVoteRepository voteRepo, 
			JdbcNotificationRepository notifRepo) {
		this.accountRepo = accountRepo;
		this.topicRepo = topicRepo;
		this.commentRepo = commentRepo;
		this.classifier = classifier;
		this.voteRepo = voteRepo;
		this.notifRepo = notifRepo;
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView welcome(Locale locale, Model model, HttpSession session) {
		String username = (String) session.getAttribute("uname");
		int comments = commentRepo.getCommentsForUser(username);
		
		model.addAttribute("topicForm", new TopicForm());
		model.addAttribute("recentTopics", topicRepo.getRecentTopics());
		model.addAttribute("popularTopics", topicRepo.getPopularTopics());
		model.addAttribute("notifs", notifRepo.getAllNotifs(username));
		
		session.setAttribute("comments", comments);		
		session.setAttribute("votes", voteRepo.getVotesForUser(username));
		session.setAttribute("rank", commentRepo.getRankForUser(username, comments));
				       
	  return new ModelAndView("welcome", model.asMap());
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ModelAndView newTopic(HttpServletRequest request, Locale locale, 
			Model model, TopicForm topicForm, HttpSession session) {
		if (topicForm.validate()) {
			boolean success = true;
			String commenter = 	(String) session.getAttribute("uname");
			String photoUrl = (String) session.getAttribute("photoUrl");
			long newTopicId = 0;
			try {
				newTopicId = topicRepo.createTopic(topicForm.getTitle());
				String sentiment = determineStentiment(topicForm.getInitialComment());
				Comment newComment = new Comment(0, newTopicId, 
						accountRepo.findAccountByUsername(commenter),
						topicForm.getInitialComment(), 0, 0, new Date(),
						photoUrl, true, sentiment, 7.0);
				success = commentRepo.createComment(newComment);
				if (!success) {
					topicRepo.deleteTopic(topicForm.getTitle());
				}
			} catch (TopicAlreadyExistsException e) {
				success = false;
			}
			if (success) {
				return new ModelAndView("redirect:/topic?id="+newTopicId, 
						model.asMap());
			}
		}
		return new ModelAndView("redirect:/", model.asMap());
	}
	
	private String determineStentiment(String text) {
		return classifier.getSentiment(text).toString().toLowerCase();
	}
	
	@RequestMapping(value = "/notifs", method = RequestMethod.GET)
	public @ResponseBody Notification[] updateNotifs(HttpSession session) {
		String username = (String) session.getAttribute("uname");
		List<Notification> notifsList = notifRepo.getAllNotifs(username);
		Notification[] notifs = new Notification[notifsList.size()];
		notifsList.toArray(notifs);
		return notifs;
	}
	
	@RequestMapping(value = "/topics", method = RequestMethod.GET)
	public ModelAndView topics(String order, Model model, HttpSession session) {
		model.addAttribute("topics", topicRepo.getOrdererTopics(order));
		return new ModelAndView("topics", model.asMap());
	}
}
