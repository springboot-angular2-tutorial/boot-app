package com.myapp.repository

import com.myapp.config.QueryDSLConfig
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("test")
@Import(value = [QueryDSLConfig])
@DataJpaTest
abstract class BaseRepositoryTest extends Specification {
}
