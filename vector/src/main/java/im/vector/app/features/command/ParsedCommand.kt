

package im.vector.app.features.command

import im.vector.app.features.home.room.detail.ChatEffect
import org.matrix.android.sdk.api.session.identity.ThreePid


sealed interface ParsedCommand {
    
    object ErrorNotACommand : ParsedCommand

    object ErrorEmptySlashCommand : ParsedCommand

    class ErrorCommandNotSupportedInThreads(val command: Command) : ParsedCommand

    
    data class ErrorUnknownSlashCommand(val slashCommand: String) : ParsedCommand

    
    data class ErrorSyntax(val command: Command) : ParsedCommand

    

    data class SendPlainText(val message: CharSequence) : ParsedCommand
    data class SendEmote(val message: CharSequence) : ParsedCommand
    data class SendRainbow(val message: CharSequence) : ParsedCommand
    data class SendRainbowEmote(val message: CharSequence) : ParsedCommand
    data class BanUser(val userId: String, val reason: String?) : ParsedCommand
    data class UnbanUser(val userId: String, val reason: String?) : ParsedCommand
    data class IgnoreUser(val userId: String) : ParsedCommand
    data class UnignoreUser(val userId: String) : ParsedCommand
    data class SetUserPowerLevel(val userId: String, val powerLevel: Int?) : ParsedCommand
    data class ChangeRoomName(val name: String) : ParsedCommand
    data class Invite(val userId: String, val reason: String?) : ParsedCommand
    data class Invite3Pid(val threePid: ThreePid) : ParsedCommand
    data class JoinRoom(val roomAlias: String, val reason: String?) : ParsedCommand
    data class PartRoom(val roomAlias: String?) : ParsedCommand
    data class ChangeTopic(val topic: String) : ParsedCommand
    data class RemoveUser(val userId: String, val reason: String?) : ParsedCommand
    data class ChangeDisplayName(val displayName: String) : ParsedCommand
    data class ChangeDisplayNameForRoom(val displayName: String) : ParsedCommand
    data class ChangeRoomAvatar(val url: String) : ParsedCommand
    data class ChangeAvatarForRoom(val url: String) : ParsedCommand
    data class SetMarkdown(val enable: Boolean) : ParsedCommand
    object ClearScalarToken : ParsedCommand
    data class SendSpoiler(val message: String) : ParsedCommand
    data class SendShrug(val message: CharSequence) : ParsedCommand
    data class SendLenny(val message: CharSequence) : ParsedCommand
    object DiscardSession : ParsedCommand
    data class ShowUser(val userId: String) : ParsedCommand
    data class SendChatEffect(val chatEffect: ChatEffect, val message: String) : ParsedCommand
    data class CreateSpace(val name: String, val invitees: List<String>) : ParsedCommand
    data class AddToSpace(val spaceId: String) : ParsedCommand
    data class JoinSpace(val spaceIdOrAlias: String) : ParsedCommand
    data class LeaveRoom(val roomId: String) : ParsedCommand
    data class UpgradeRoom(val newVersion: String) : ParsedCommand
}
