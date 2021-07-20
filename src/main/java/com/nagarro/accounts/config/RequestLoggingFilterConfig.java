package com.nagarro.accounts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Spring fonctionality to log incoming request
 * active with application.properties 
 * logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
 * @author claud
 *
 */
@Configuration
public class RequestLoggingFilterConfig {

	/**
	 * Log the incoming request
	 * @return
	 */
	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(false);
		filter.setAfterMessagePrefix("RequestData : ");
		return filter;
	}
}