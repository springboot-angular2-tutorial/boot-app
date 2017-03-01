package com.myapp.domain

import com.myapp.generated.tables.records.UserStatsRecord

data class UserStats(
    val micropostCnt: Long,
    val followingCnt: Long,
    val followerCnt: Long
) {
    constructor(record: UserStatsRecord) : this(
        micropostCnt = record.micropostCnt,
        followerCnt = record.followerCnt,
        followingCnt = record.followingCnt
    )
}