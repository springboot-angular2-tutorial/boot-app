package com.myapp.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import spock.lang.Shared
import spock.lang.Specification

@WebAppConfiguration
@Transactional
@ActiveProfiles("test")
abstract class BaseControllerTest extends Specification {

    @SuppressWarnings("GroovyUnusedDeclaration")
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Shared
    private MockMvc mockMvc

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controllers()).build()
    }

    ResultActions perform(RequestBuilder requestBuilder) {
        return mockMvc.perform(requestBuilder)
    }

    abstract def controllers()
}
