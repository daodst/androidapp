

package im.vector.app.features.home.room.detail.timeline.item

sealed class PollOptionViewState(open val optionId: String,
                                 open val optionAnswer: String) {
    
    data class PollSending(override val optionId: String,
                           override val optionAnswer: String
    ) : PollOptionViewState(optionId, optionAnswer)

    
    data class PollReady(override val optionId: String,
                         override val optionAnswer: String
    ) : PollOptionViewState(optionId, optionAnswer)

    
    data class PollVoted(override val optionId: String,
                         override val optionAnswer: String,
                         val voteCount: Int,
                         val votePercentage: Double,
                         val isSelected: Boolean
    ) : PollOptionViewState(optionId, optionAnswer)

    
    data class PollEnded(override val optionId: String,
                         override val optionAnswer: String,
                         val voteCount: Int,
                         val votePercentage: Double,
                         val isWinner: Boolean
    ) : PollOptionViewState(optionId, optionAnswer)

    
    data class PollUndisclosed(override val optionId: String,
                               override val optionAnswer: String,
                               val isSelected: Boolean
    ) : PollOptionViewState(optionId, optionAnswer)
}
