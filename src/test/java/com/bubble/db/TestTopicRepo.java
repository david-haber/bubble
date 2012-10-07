package com.bubble.db;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.bubble.config.db.DBTestConfig;
import com.bubble.db.topic.JdbcTopicRepository;
import com.bubble.db.topic.Topic;
import com.bubble.db.topic.TopicAlreadyExistsException;
import com.bubble.db.topic.TopicRepository;

/*@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DBTestConfig.class}, loader=AnnotationConfigContextLoader.class)
@ActiveProfiles("test")*/
public class TestTopicRepo {
	
	@Autowired
	JdbcTemplate template;
	
	TopicRepository topicRepo;
	
	@Before
	public void beforeClass() {
	/*	topicRepo = new JdbcTopicRepository(template);
		topicRepo.getAllTopics(null);*/
	}
	
	@Test
	public void checkCannotCreateSameTopicTwice() {
		// Database already contains iPod
	/*	topicRepo.createTopic("iPod");*/
		// Shouldn't accept ipod
		
	}
	
	public void checkCreateOddTopicWorks() throws TopicAlreadyExistsException {
/*		String topicName = "Wisher's Pride";
		topicRepo.createTopic(topicName);
		List<Topic> topics = topicRepo.getAllTopics(null);
		boolean spotted = false;
		for (Topic t: topics) {
			if (t.getTitle().equals(topicName)) {
				spotted = true;
				break;
			}
		}
		assertTrue(spotted);*/
	}
		
	@Test
	public void shouldDeleteTopicThatExists() throws TopicAlreadyExistsException {
/*		topicRepo.createTopic("Nexus");
		topicRepo.deleteTopic("Nexus");
		List<Topic> topics = topicRepo.getAllTopics(null);
		for (Topic t: topics) {
			assertFalse(t.getTitle().equals("Nexus"));
		}*/
	}
	
	@Test
	public void shouldHandleDeletingATopicThatDoesntExist() {
		/*assertFalse(topicRepo.deleteTopic("BobbyD"));*/
	}

	
	
}
