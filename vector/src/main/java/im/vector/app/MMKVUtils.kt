package im.vector.app

import com.tencent.mmkv.MMKV

const val ROOM_JOIN_WELCOME_FLAG = "room_join_welcome_flag"

class MMKVUtils {

    fun getSp(): MMKV? {
        return MMKV.defaultMMKV()
    }

    fun putNewRoomId(roomId: String) {
        val strings = getSp()?.getStringSet(ROOM_JOIN_WELCOME_FLAG, mutableSetOf<String>()) ?: mutableSetOf<String>()
        if (!strings.contains(roomId)) {
            strings.add(roomId)
            getSp()?.putStringSet(ROOM_JOIN_WELCOME_FLAG, strings)
        }
    }

    fun findRoomId(roomId: String): Boolean {
        val strings = getSp()?.getStringSet(ROOM_JOIN_WELCOME_FLAG, mutableSetOf<String>()) ?: mutableSetOf<String>()
        return strings.contains(roomId)
    }

    fun removeRoomId(roomId: String) {
        val strings = getSp()?.getStringSet(ROOM_JOIN_WELCOME_FLAG, mutableSetOf<String>()) ?: mutableSetOf<String>()
        if (strings.contains(roomId)) {
            strings.remove(roomId)
            getSp()?.putStringSet(ROOM_JOIN_WELCOME_FLAG, strings)
        }
    }
}
