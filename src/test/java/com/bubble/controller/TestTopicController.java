package com.bubble.controller;

//import static org.mockito.Mockito.*;

public class TestTopicController {
/*	
	//Dependencies
	Twitter t;
	JdbcVoteRepository voteRepo;
	JdbcCommentRepository commentRepo;
	JdbcTopicRepository topicRepo;
	JdbcAccountRepository accountRepo;
	Classifier classifier;
	
	//Test subject
	TopicController c;
	
	@Before
	public void before() {
		t = mock(Twitter.class);
		voteRepo = mock(JdbcVoteRepository.class);
		commentRepo = mock(JdbcCommentRepository.class);
		topicRepo = mock(JdbcTopicRepository.class);
		accountRepo = mock(JdbcAccountRepository.class);
		classifier = mock(Classifier.class);
		c = new TopicController(t, accountRepo, voteRepo, commentRepo, topicRepo, classifier);
	}

	@Test
	public void testAddingAReply() {
		String commenter = "oli";
		String message = "I love this";
		int topicId = 1;
		int parentId = 32;
		MockHttpSession session = new MockHttpSession();
		session.putValue("photoUrl", "");
		Account acc = new Account("oli", "Oliver", "Wilkie", 20, "Worcester", "ozzyoli@gmail.com");
		Comment reply = c.addReply(topicId, commenter,message, parentId, true, false, session);
		//Test Add Reply returns the new comment object
		verify(classifier,times(1)).getProbability(true, message);
		verify(classifier,times(1)).getProbability(false, message);
		when(commentRepo.getLevel(parentId)).thenReturn(1);
		when(accountRepo.findAccountByUsername(commenter)).thenReturn(acc);
		verify(commentRepo).createComment((Comment)anyObject());
		
		//Ensure a tweet is never sent
		verify(t,never()).timelineOperations();
		
		//Ensure Comment object has the same attributes that were attributes at the start
		assertEquals(reply.getText(), message);
		assertEquals(reply.getTopic(), topicId);
		assertEquals(reply.getParentId(), parentId);
		assertEquals(reply.getPhotoUrl(), "");
	}
	
	@Test
	public void checkTweetSentWhenTweetMeIsSet() {
		String commenter = "oli";
		String message = "I love this";
		int topicId = 1;
		int parentId = 32;
		MockHttpSession session = new MockHttpSession();
		session.putValue("photoUrl", "");
		Account acc = new Account("oli", "Oliver", "Wilkie", 20, "Worcester", "ozzyoli@gmail.com");
		when(topicRepo.getTopic(topicId)).thenReturn(new Topic("iPod Nano"));
		TimelineOperations timelineOperations = mock(TimelineOperations.class);
		when(t.timelineOperations()).thenReturn(timelineOperations);

		Comment reply = c.addReply(topicId, commenter,message, parentId, true, true, session);

		//Ensure a tweet is sent
		verify(timelineOperations, times(1)).updateStatus(anyString());

	}
	
	@Test
	public void testTopicId() {
		Model m = new ExtendedModelMap();
		MockHttpSession session = new MockHttpSession();
		session.putValue("uname", "ozzyoli");
		CommentTree commentTree = new CommentTree(new Comment(0, 0, null, null, 0, 0, new Date(), null, false, null));
		when(commentRepo.getCommentsAsTree(5, "ozzyoli")).thenReturn(commentTree);
		ModelAndView result = c.topic(5, m, session);
		Map<String,Object> resultModel = result.getModel();
		assertEquals(result.getViewName(), "topic");
	}
	
	
*/
}
