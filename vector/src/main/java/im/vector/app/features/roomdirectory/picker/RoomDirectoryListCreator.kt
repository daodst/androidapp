

package im.vector.app.features.roomdirectory.picker

import im.vector.app.R
import im.vector.app.core.resources.StringArrayProvider
import im.vector.app.features.roomdirectory.RoomDirectoryData
import im.vector.app.features.roomdirectory.RoomDirectoryServer
import org.matrix.android.sdk.api.MatrixPatterns.getDomain
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.thirdparty.ThirdPartyProtocol
import javax.inject.Inject

class RoomDirectoryListCreator @Inject constructor(
        private val stringArrayProvider: StringArrayProvider,
        private val session: Session
) {

    fun computeDirectories(thirdPartyProtocolData: Map<String, ThirdPartyProtocol>,
                           customHomeservers: Set<String>): List<RoomDirectoryServer> {
        val result = ArrayList<RoomDirectoryServer>()

        val protocols = ArrayList<RoomDirectoryData>()

        
        val userHsName = session.myUserId.getDomain()

        
        protocols.add(
                RoomDirectoryData(
                        homeServer = null,
                        displayName = RoomDirectoryData.MATRIX_PROTOCOL_NAME,
                        includeAllNetworks = false
                )
        )

        
        thirdPartyProtocolData.forEach {
            it.value.instances?.forEach { thirdPartyProtocolInstance ->
                protocols.add(
                        RoomDirectoryData(
                                homeServer = null,
                                displayName = thirdPartyProtocolInstance.desc ?: "",
                                thirdPartyInstanceId = thirdPartyProtocolInstance.instanceId,
                                includeAllNetworks = false,
                                
                                avatarUrl = thirdPartyProtocolInstance.icon ?: it.value.icon
                        )
                )
            }
        }

        
        protocols.add(
                RoomDirectoryData(
                        homeServer = null,
                        displayName = RoomDirectoryData.MATRIX_PROTOCOL_NAME,
                        includeAllNetworks = true
                )
        )

        result.add(
                RoomDirectoryServer(
                        serverName = userHsName,
                        isUserServer = true,
                        isManuallyAdded = false,
                        protocols = protocols
                )
        )

        
        stringArrayProvider.getStringArray(R.array.room_directory_servers)
                .filter { it != userHsName }
                .forEach {
                    
                    result.add(
                            RoomDirectoryServer(
                                    serverName = it,
                                    isUserServer = false,
                                    isManuallyAdded = false,
                                    protocols = listOf(
                                            RoomDirectoryData(
                                                    homeServer = it,
                                                    displayName = RoomDirectoryData.MATRIX_PROTOCOL_NAME,
                                                    includeAllNetworks = false
                                            )
                                    )
                            )
                    )
                }

        
        customHomeservers
                .forEach {
                    
                    result.add(
                            RoomDirectoryServer(
                                    serverName = it,
                                    isUserServer = false,
                                    isManuallyAdded = true,
                                    protocols = listOf(
                                            RoomDirectoryData(
                                                    homeServer = it,
                                                    displayName = RoomDirectoryData.MATRIX_PROTOCOL_NAME,
                                                    includeAllNetworks = false
                                            )
                                    )
                            )
                    )
                }

        return result
    }
}
