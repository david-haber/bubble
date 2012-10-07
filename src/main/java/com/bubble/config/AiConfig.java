package com.bubble.config;

/**
 * Configurations for Sentiment Classifier
 */

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bubble.classifier.Classifier;
import com.bubble.corpus.AnalyzedCorpus;

@Configuration
public class AiConfig {

	private final String posCorpusLocation = "/posCorpus";
	private final String negCorpusLocation = "/negCorpus";
	
	private static final Logger logger = LoggerFactory.getLogger(AiConfig.class);

	@Bean
	public Classifier classifier() throws Exception {
		AnalyzedCorpus posCorpus = AnalyzedCorpus.readFromFile(getResourceFile(posCorpusLocation));
		AnalyzedCorpus negCorpus = AnalyzedCorpus.readFromFile(getResourceFile(negCorpusLocation));
		if (posCorpus == null || negCorpus == null) {
			logger.error("Unable to load posCorpus or negCorpus files");
			throw new Exception("AI Bean unable to load posCorpus or negCorpus files");
		}
		// create classifier with default settings
		Classifier c = new Classifier(posCorpus, negCorpus);
		return c;
	}
	
	private File getResourceFile(String location) {
		URL url = getClass().getResource(location);
		File f;
		try {
		  f = new File(url.toURI());
		} catch(URISyntaxException e) {
		  f = new File(url.getPath());
		}
		return f;
	}

}
