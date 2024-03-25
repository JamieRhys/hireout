package com.sycosoft.hireout.configurations

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment

@Configuration
@Profile("test")
class TestConfiguration(private val env: Environment) {
    private val logger: Logger = LoggerFactory.getLogger(DevConfiguration::class.java)

    init {
        logger.info("Test configuration loaded.")
    }
}