

package org.matrix.android.sdk.api.session.room.powerlevels

sealed class Role(open val value: Int) : Comparable<Role> {
    object Admin : Role(100)
    object Moderator : Role(50)
    object Default : Role(0)
    data class Custom(override val value: Int) : Role(value)

    override fun compareTo(other: Role): Int {
        return value.compareTo(other.value)
    }

    companion object {

        
        fun fromValue(value: Int, default: Int): Role {
            return when (value) {
                Admin.value     -> Admin
                Moderator.value -> Moderator
                Default.value,
                default         -> Default
                else            -> Custom(value)
            }
        }
    }
}
