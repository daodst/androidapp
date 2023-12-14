

package org.matrix.android.sdk.api.session.crypto.model

class MXUsersDevicesMap<E> {

    
    val map = HashMap<String , HashMap<String , E>>()

    
    val userIds: List<String>
        get() = map.keys.toList()

    val isEmpty: Boolean
        get() = map.isEmpty()

    
    fun getUserDeviceIds(userId: String?): List<String>? {
        return if (!userId.isNullOrBlank() && map.containsKey(userId)) {
            map[userId]!!.keys.toList()
        } else null
    }

    
    fun getObject(userId: String?, deviceId: String?): E? {
        return if (!userId.isNullOrBlank() && !deviceId.isNullOrBlank()) {
            map[userId]?.get(deviceId)
        } else null
    }

    
    fun setObject(userId: String?, deviceId: String?, o: E?) {
        if (null != o && userId?.isNotBlank() == true && deviceId?.isNotBlank() == true) {
            val devices = map.getOrPut(userId) { HashMap() }
            devices[deviceId] = o
        }
    }

    
    fun setObjects(userId: String?, objectsPerDevices: Map<String, E>?) {
        if (!userId.isNullOrBlank()) {
            if (null == objectsPerDevices) {
                map.remove(userId)
            } else {
                map[userId] = HashMap(objectsPerDevices)
            }
        }
    }

    
    fun removeUserObjects(userId: String?) {
        if (!userId.isNullOrBlank()) {
            map.remove(userId)
        }
    }

    
    fun removeAllObjects() {
        map.clear()
    }

    
    fun addEntriesFromMap(other: MXUsersDevicesMap<E>?) {
        if (null != other) {
            map.putAll(other.map)
        }
    }

    override fun toString(): String {
        return "MXUsersDevicesMap $map"
    }
}

inline fun <T> MXUsersDevicesMap<T>.forEach(action: (String, String, T) -> Unit) {
    userIds.forEach { userId ->
        getUserDeviceIds(userId)?.forEach { deviceId ->
            getObject(userId, deviceId)?.let {
                action(userId, deviceId, it)
            }
        }
    }
}
