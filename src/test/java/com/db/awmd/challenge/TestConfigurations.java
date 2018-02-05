package com.db.awmd.challenge;

import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.service.NotificationService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * This is configuration of Unit tests and scanned packages.
 * This class must not be loaded as part of actual web deployment.
 */
@Configuration
@ComponentScan(basePackages = {"com.db.awmd.challenge"})
public class TestConfigurations {

    /**
     * This method creates mock bean of {@link com.db.awmd.challenge.service.NotificationService}
     * @return NotificationService {@link com.db.awmd.challenge.service.NotificationService}
     */
    @Bean
    public NotificationService emailNotificationService() {
        return Mockito.mock(NotificationService.class);
    }
}
