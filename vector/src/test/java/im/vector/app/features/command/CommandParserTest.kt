

package im.vector.app.features.command

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class CommandParserTest {
    @Test
    fun parseSlashCommandEmpty() {
        test("/", ParsedCommand.ErrorEmptySlashCommand)
    }

    @Test
    fun parseSlashCommandUnknown() {
        test("/unknown", ParsedCommand.ErrorUnknownSlashCommand("/unknown"))
        test("/unknown with param", ParsedCommand.ErrorUnknownSlashCommand("/unknown"))
    }

    @Test
    fun parseSlashCommandNotACommand() {
        test("", ParsedCommand.ErrorNotACommand)
        test("test", ParsedCommand.ErrorNotACommand)
        test("
    }

    @Test
    fun parseSlashCommandEmote() {
        test("/me test", ParsedCommand.SendEmote("test"))
        test("/me", ParsedCommand.ErrorSyntax(Command.EMOTE))
    }

    @Test
    fun parseSlashCommandRemove() {
        
        test("/remove @foo:bar", ParsedCommand.RemoveUser("@foo:bar", null))
        
        test("/remove @foo:bar a reason", ParsedCommand.RemoveUser("@foo:bar", "a reason"))
        
        test("/remove @foo:bar    a    reason    ", ParsedCommand.RemoveUser("@foo:bar", "a    reason"))
        
        test("/kick @foo:bar", ParsedCommand.RemoveUser("@foo:bar", null))
        
        test("/remove", ParsedCommand.ErrorSyntax(Command.REMOVE_USER))
    }

    private fun test(message: String, expectedResult: ParsedCommand) {
        val commandParser = CommandParser()
        val result = commandParser.parseSlashCommand(message, false)
        result shouldBeEqualTo expectedResult
    }
}
