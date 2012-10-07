package com.bubble.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

@Configuration
@EnableWebMvc
@ComponentScan("com.bubble.**")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//Directly maps /signin to signin.jsp
		registry.addViewController("/signin");
	}

	@Bean
	public ViewResolver basicViewResolver() {
		UrlBasedViewResolver viewResolver = 
				new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setOrder(2);
		return viewResolver;
	}

	@Bean
	public TilesConfigurer tilesConfig() {
		TilesConfigurer t = new TilesConfigurer();
		t.setDefinitions(new String[]{"/WEB-INF/defs/basic.xml"});
		return t;
	}

	@Bean
	public ViewResolver viewResolver() {
		UrlBasedViewResolver res = new UrlBasedViewResolver();
		res.setViewClass(TilesView.class);
		res.setOrder(1);
		return res;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").
		addResourceLocations("/resources/");
	}

}
