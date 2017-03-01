package com.myapp.repository.exception

import org.springframework.dao.DuplicateKeyException

class RelationshipDuplicatedException(msg: String) : DuplicateKeyException(msg)
