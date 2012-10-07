package com.bubble.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GraphicsController {
	
	@RequestMapping(value = "/bubblegraphics", method = RequestMethod.GET)
	public ModelAndView showBubbleGraphics(int topic, int score, Model model) {
		model.addAttribute("currentTopic", topic);
		model.addAttribute("score", score);
		return new ModelAndView("bubblegraphics", model.asMap());
	}

	@RequestMapping(value = "/textflowgraphics", method = RequestMethod.GET)
	public ModelAndView showTextFlowGraphics(int topic, int score, Model model) {
		model.addAttribute("currentTopic", topic);
		model.addAttribute("score", score);
		return new ModelAndView("textflowgraphics", model.asMap());
	}
	
}
