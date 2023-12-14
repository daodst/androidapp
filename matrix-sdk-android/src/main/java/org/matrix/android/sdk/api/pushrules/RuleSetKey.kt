

package org.matrix.android.sdk.api.pushrules


enum class RuleSetKey(val value: String) {
    CONTENT("content"),
    OVERRIDE("override"),
    ROOM("room"),
    SENDER("sender"),
    UNDERRIDE("underride")
}


typealias RuleKind = RuleSetKey
