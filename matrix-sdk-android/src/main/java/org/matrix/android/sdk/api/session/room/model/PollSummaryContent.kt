
package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PollSummaryContent(
        val myVote: String? = null,
        
        
        val votes: List<VoteInfo>? = null,
        val votesSummary: Map<String, VoteSummary>? = null,
        val totalVotes: Int = 0,
        val winnerVoteCount: Int = 0
)

@JsonClass(generateAdapter = true)
data class VoteSummary(
        val total: Int = 0,
        val percentage: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class VoteInfo(
        val userId: String,
        val option: String,
        val voteTimestamp: Long
)
