package org.ht.iops.events.listner;

import java.util.List;

import javax.annotation.PostConstruct;

import org.ht.iops.db.beans.config.EventConfig;
import org.ht.iops.db.repository.config.EventConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class EventListnerProcessor {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(EventListnerProcessor.class);
	private DefaultListableBeanFactory beanFactory;

	@Autowired
	private EventConfigRepository eventConfigRepository;

	@Autowired
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof DefaultListableBeanFactory) {
			this.beanFactory = (DefaultListableBeanFactory) beanFactory;
		}
	}

	@PostConstruct
	public void registerListners() {
		List<EventConfig> configs = eventConfigRepository.findAll();
		configs.stream().filter(config -> config.isEnabled())
				.forEach(config -> registerListner(config));

	}

	private void registerListner(EventConfig config) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.rootBeanDefinition(ApplicationEventListner.class);
		builder.addPropertyValue("listnerName", config.getName());
		builder.addPropertyValue("events", config.getEvents());
		builder.addPropertyValue("emailRequired", config.isEmailRequired());
		builder.addPropertyReference("requestAdapter", config.getAdapter());
		builder.addPropertyReference("integrationService",
				"integrationService");
		beanFactory.registerBeanDefinition(config.getName(),
				builder.getBeanDefinition());
		LOGGER.info("Registering listner " + config.getName() + " for events "
				+ config.getEvents() + ", and adapter " + config.getAdapter());
	}
}
