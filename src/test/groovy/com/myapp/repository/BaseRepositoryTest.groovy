package com.myapp.repository

import com.myapp.config.QueryDSLConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Transactional
@ActiveProfiles("test")
@ContextConfiguration(classes = [RepositoryTestConfig, QueryDSLConfig])
abstract class BaseRepositoryTest extends Specification {
}
