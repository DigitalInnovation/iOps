package org.ht.iops.configurations;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration
@Conditional(MailConfiguration.MailCondition.class)
@ImportResource("classpath:mail-config.xml")
public class MailConfiguration {
	public static class MailCondition implements ConfigurationCondition {
		@Override
		public boolean matches(ConditionContext context,
				AnnotatedTypeMetadata metadata) {
			return null == System.getProperty("iops.testmode");
		}

		@Override
		public ConfigurationPhase getConfigurationPhase() {
			return ConfigurationPhase.PARSE_CONFIGURATION;
		}
	}
}
