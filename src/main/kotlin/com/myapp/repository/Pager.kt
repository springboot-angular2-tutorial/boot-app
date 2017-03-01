package com.myapp.repository

import com.myapp.dto.request.PageParams
import org.jooq.Condition
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL

interface Pager {

    fun <R : Record> PageParams.toCondition(tableField: TableField<R, Long>): Condition {
        return DSL.trueCondition()
            .let { if (sinceId == null) it else it.and(tableField.gt(sinceId)) }
            .let { if (maxId == null) it else it.and(tableField.lt(maxId)) }
    }

}