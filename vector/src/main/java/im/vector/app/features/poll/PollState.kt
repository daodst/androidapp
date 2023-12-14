

package im.vector.app.features.poll

sealed interface PollState {
    object Sending : PollState
    object Ready : PollState
    data class Voted(val votes: Int) : PollState
    object Undisclosed : PollState
    object Ended : PollState

    fun isVotable() = this !is Sending && this !is Ended
}
