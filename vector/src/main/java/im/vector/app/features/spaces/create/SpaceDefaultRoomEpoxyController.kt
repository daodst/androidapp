

package im.vector.app.features.spaces.create

import com.airbnb.epoxy.TypedEpoxyController
import com.google.android.material.textfield.TextInputLayout
import im.vector.app.R
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.ui.list.ItemStyle
import im.vector.app.core.ui.list.genericFooterItem
import im.vector.app.features.form.formEditTextItem
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import javax.inject.Inject

class SpaceDefaultRoomEpoxyController @Inject constructor(
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider
) : TypedEpoxyController<CreateSpaceState>() {

    var listener: Listener? = null


    override fun buildModels(data: CreateSpaceState?) {
        val host = this
        genericFooterItem {
            id("info_help_header")
            style(ItemStyle.TITLE)
            text(
                    if (data?.spaceType == SpaceType.Public) {
                        host.stringProvider.getString(R.string.create_spaces_room_public_header, data.name)
                    } else {
                        host.stringProvider.getString(R.string.create_spaces_room_private_header)
                    }.toEpoxyCharSequence()
            )
            textColor(host.colorProvider.getColorFromAttribute(R.attr.vctr_content_primary))
        }

        genericFooterItem {
            id("info_help")
            text(
                    host.stringProvider.getString(
                            if (data?.spaceType == SpaceType.Public) {
                                R.string.create_spaces_room_public_header_desc
                            } else {
                                R.string.create_spaces_room_private_header_desc
                            }
                    ).toEpoxyCharSequence()
            )
            textColor(host.colorProvider.getColorFromAttribute(R.attr.vctr_content_secondary))
        }

        val firstRoomName = data?.defaultRooms?.get(0)
        formEditTextItem {
            id("roomName1")
            enabled(true)
            value(firstRoomName)
            hint(host.stringProvider.getString(R.string.create_room_name_section))
            endIconMode(TextInputLayout.END_ICON_CLEAR_TEXT)
            onTextChange { text ->
                host.listener?.onNameChange(0, text)
            }
        }

        val secondRoomName = data?.defaultRooms?.get(1)
        formEditTextItem {
            id("roomName2")
            enabled(true)
            value(secondRoomName)
            hint(host.stringProvider.getString(R.string.create_room_name_section))
            endIconMode(TextInputLayout.END_ICON_CLEAR_TEXT)
            onTextChange { text ->
                host.listener?.onNameChange(1, text)
            }
        }

        val thirdRoomName = data?.defaultRooms?.get(2)
        formEditTextItem {
            id("roomName3")
            enabled(true)
            value(thirdRoomName)
            hint(host.stringProvider.getString(R.string.create_room_name_section))
            endIconMode(TextInputLayout.END_ICON_CLEAR_TEXT)
            onTextChange { text ->
                host.listener?.onNameChange(2, text)
            }
        }
    }

    interface Listener {
        fun onNameChange(index: Int, newName: String)
    }
}
