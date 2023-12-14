

package org.matrix.android.sdk.internal.session.room.send

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.util.TextContent
import org.matrix.android.sdk.common.TestRoomDisplayNameFallbackProvider
import org.matrix.android.sdk.internal.session.displayname.DisplayNameResolver
import org.matrix.android.sdk.internal.session.room.send.pills.MentionLinkSpecComparator
import org.matrix.android.sdk.internal.session.room.send.pills.TextPillsUtils


@Suppress("SpellCheckingInspection")
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
class MarkdownParserTest : InstrumentedTest {

    
    private val markdownParser = MarkdownParser(
            Parser.builder().build(),
            Parser.builder().build(),
            HtmlRenderer.builder().softbreak("<br />").build(),
            TextPillsUtils(
                    MentionLinkSpecComparator(),
                    DisplayNameResolver(
                            MatrixConfiguration(
                                    applicationFlavor = "TestFlavor",
                                    roomDisplayNameFallbackProvider = TestRoomDisplayNameFallbackProvider()
                            )
                    ),
                    TestPermalinkService()
            )
    )

    @Test
    fun parseNoMarkdown() {
        testIdentity("")
        testIdentity("a")
        testIdentity("1")
        testIdentity("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et " +
                "dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea com" +
                "modo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pari" +
                "atur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
    }

    @Test
    fun parseSpaces() {
        testIdentity(" ")
        testIdentity("  ")
        testIdentity("\n")
    }

    @Test
    fun parseNewLines() {
        testIdentity("line1\nline2")
        testIdentity("line1\nline2\nline3")
    }

    @Test
    fun parseBold() {
        testType(
                name = "bold",
                markdownPattern = "**",
                htmlExpectedTag = "strong"
        )
    }

    @Test
    fun parseBoldNewLines() {
        testTypeNewLines(
                name = "bold",
                markdownPattern = "**",
                htmlExpectedTag = "strong"
        )
    }

    @Test
    fun parseItalic() {
        testType(
                name = "italic",
                markdownPattern = "*",
                htmlExpectedTag = "em"
        )
    }

    @Test
    fun parseItalicNewLines() {
        testTypeNewLines(
                name = "italic",
                markdownPattern = "*",
                htmlExpectedTag = "em"
        )
    }

    @Test
    fun parseItalic2() {
        
        "_italic_".let { markdownParser.parse(it).expect(it, "<em>italic</em>") }
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun parseStrike_not_passing() {
        testType(
                name = "strike",
                markdownPattern = "~~",
                htmlExpectedTag = "del"
        )
    }

    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun parseStrikeNewLines() {
        testTypeNewLines(
                name = "strike",
                markdownPattern = "~~",
                htmlExpectedTag = "del"
        )
    }

    @Test
    fun parseCode() {
        testType(
                name = "code",
                markdownPattern = "`",
                htmlExpectedTag = "code"
        )
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun parseCodeNewLines_not_passing() {
        testTypeNewLines(
                name = "code",
                markdownPattern = "```",
                htmlExpectedTag = "code",
                softBreak = "\n"
        )
    }

    @Test
    fun parseCode2() {
        testType(
                name = "code",
                markdownPattern = "``",
                htmlExpectedTag = "code"
        )
    }

    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun parseCode2NewLines_not_passing() {
        testTypeNewLines(
                name = "code",
                markdownPattern = "``",
                htmlExpectedTag = "code"
        )
    }

    @Test
    fun parseCode3() {
        testType(
                name = "code",
                markdownPattern = "```",
                htmlExpectedTag = "code"
        )
    }

    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun parseCode3NewLines_not_passing() {
        testTypeNewLines(
                name = "code",
                markdownPattern = "```",
                htmlExpectedTag = "code"
        )
    }

    @Test
    fun parseUnorderedList() {
        "- item1".let { markdownParser.parse(it).expect(it, "<ul>\n<li>item1</li>\n</ul>") }
        "- item1\n- item2".let { markdownParser.parse(it).expect(it, "<ul>\n<li>item1</li>\n<li>item2</li>\n</ul>") }
    }

    @Test
    fun parseOrderedList() {
        "1. item1".let { markdownParser.parse(it).expect(it, "<ol>\n<li>item1</li>\n</ol>") }
        "1. item1\n2. item2".let { markdownParser.parse(it).expect(it, "<ol>\n<li>item1</li>\n<li>item2</li>\n</ol>") }
    }

    @Test
    fun parseHorizontalLine() {
        "---".let { markdownParser.parse(it).expect(it, "<hr />") }
    }

    @Test
    fun parseH2AndContent() {
        "a\n---\nb".let { markdownParser.parse(it).expect(it, "<h2>a</h2>\n<p>b</p>") }
    }

    @Test
    fun parseQuote() {
        "> quoted".let { markdownParser.parse(it).expect(it, "<blockquote>\n<p>quoted</p>\n</blockquote>") }
    }

    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun parseQuote_not_passing() {
        "> quoted\nline2".let { markdownParser.parse(it).expect(it, "<blockquote><p>quoted<br />line2</p></blockquote>") }
    }

    @Test
    fun parseBoldItalic() {
        "*italic* **bold**".let { markdownParser.parse(it).expect(it, "<em>italic</em> <strong>bold</strong>") }
        "**bold** *italic*".let { markdownParser.parse(it).expect(it, "<strong>bold</strong> <em>italic</em>") }
    }

    @Test
    fun parseHead() {
        "# head1".let { markdownParser.parse(it).expect(it, "<h1>head1</h1>") }
        "## head2".let { markdownParser.parse(it).expect(it, "<h2>head2</h2>") }
        "### head3".let { markdownParser.parse(it).expect(it, "<h3>head3</h3>") }
        "#### head4".let { markdownParser.parse(it).expect(it, "<h4>head4</h4>") }
        "##### head5".let { markdownParser.parse(it).expect(it, "<h5>head5</h5>") }
        "###### head6".let { markdownParser.parse(it).expect(it, "<h6>head6</h6>") }
    }

    @Test
    fun parseHeads() {
        "# head1\n# head2".let { markdownParser.parse(it).expect(it, "<h1>head1</h1>\n<h1>head2</h1>") }
    }

    @Test
    fun parseBoldNewLines2() {
        "**bold**\nline2".let { markdownParser.parse(it).expect(it, "<strong>bold</strong><br />line2") }
    }

    @Test
    fun parseLinks() {
        "[link](target)".let { markdownParser.parse(it).expect(it, """<a href="target">link</a>""") }
    }

    @Test
    fun parseParagraph() {
        "# head\ncontent".let { markdownParser.parse(it).expect(it, "<h1>head</h1>\n<p>content</p>") }
    }

    private fun testIdentity(text: String) {
        markdownParser.parse(text).expect(text, null)
    }

    private fun testType(name: String,
                         markdownPattern: String,
                         htmlExpectedTag: String) {
        
        "$markdownPattern$name$markdownPattern"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "<$htmlExpectedTag>$name</$htmlExpectedTag>")
                }

        
        "$markdownPattern$name$markdownPattern and $markdownPattern$name bis$markdownPattern"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "<$htmlExpectedTag>$name</$htmlExpectedTag> and <$htmlExpectedTag>$name bis</$htmlExpectedTag>")
                }

        val textBefore = "a"
        val textAfter = "b"

        
        "$textBefore$markdownPattern$name$markdownPattern"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "$textBefore<$htmlExpectedTag>$name</$htmlExpectedTag>")
                }

        
        "$textBefore $markdownPattern$name$markdownPattern"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "$textBefore <$htmlExpectedTag>$name</$htmlExpectedTag>")
                }

        
        "$markdownPattern$name$markdownPattern$textAfter"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "<$htmlExpectedTag>$name</$htmlExpectedTag>$textAfter")
                }

        
        "$markdownPattern$name$markdownPattern $textAfter"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "<$htmlExpectedTag>$name</$htmlExpectedTag> $textAfter")
                }

        
        "$textBefore$markdownPattern$name$markdownPattern$textAfter"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "a<$htmlExpectedTag>$name</$htmlExpectedTag>$textAfter")
                }

        
        "$textBefore $markdownPattern$name$markdownPattern $textAfter"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "$textBefore <$htmlExpectedTag>$name</$htmlExpectedTag> $textAfter")
                }
    }

    private fun testTypeNewLines(name: String,
                                 markdownPattern: String,
                                 htmlExpectedTag: String,
                                 softBreak: String = "<br />") {
        
        "$markdownPattern$name\n$name$markdownPattern"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "<$htmlExpectedTag>$name$softBreak$name</$htmlExpectedTag>")
                }

        
        "$markdownPattern$name$markdownPattern\n$markdownPattern$name$markdownPattern"
                .let {
                    markdownParser.parse(it)
                            .expect(expectedText = it,
                                    expectedFormattedText = "<$htmlExpectedTag>$name</$htmlExpectedTag><br /><$htmlExpectedTag>$name</$htmlExpectedTag>")
                }
    }

    private fun TextContent.expect(expectedText: String, expectedFormattedText: String?) {
        assertEquals("TextContent are not identical", TextContent(expectedText, expectedFormattedText), this)
    }
}
