package com.bubble.controller;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public class TestGraphicsController {
	
	static GraphicsController c;
	
	@BeforeClass
	public static void before() {
		c = new GraphicsController();
	}

	@Test
	public void testShowBubbleGraphicsReturnsBubbleGraphicsView() {
		Model m = new ExtendedModelMap();
		ModelAndView result = c.showBubbleGraphics(1, 100, m);
		assertEquals(result.getViewName(),"bubblegraphics");
		Map<String,Object> model = result.getModel();
		assertEquals(model.get("currentTopic"), 1);
		assertEquals(model.get("score"), 100);
	}
	
	@Test
	public void testTextFlowGraphicsReturnsView() {
		ModelAndView result = c.showTextFlowGraphics(1, 100, new ExtendedModelMap());
		assertEquals(result.getViewName(),"textflowgraphics");
		Map<String,Object> model = result.getModel();
		assertEquals(model.get("currentTopic"), 1);
		assertEquals(model.get("score"), 100);
	}

}
