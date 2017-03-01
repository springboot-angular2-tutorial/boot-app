package com.myapp.repository.exception

import org.springframework.dao.DuplicateKeyException

class EmailDuplicatedException(msg: String) : DuplicateKeyException(msg)
