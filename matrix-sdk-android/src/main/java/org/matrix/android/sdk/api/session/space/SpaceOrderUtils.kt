

package org.matrix.android.sdk.api.session.space

import org.matrix.android.sdk.api.util.StringOrderUtils


object SpaceOrderUtils {

    data class SpaceReOrderCommand(
            val spaceId: String,
            val order: String
    )

    
    fun orderCommandsForMove(orderedSpacesToOrderMap: List<Pair<String, String?>>, movedSpaceId: String, delta: Int): List<SpaceReOrderCommand> {
        val movedIndex = orderedSpacesToOrderMap.indexOfFirst { it.first == movedSpaceId }
        if (movedIndex == -1) return emptyList()
        if (delta == 0) return emptyList()

        val targetIndex = if (delta > 0) movedIndex + delta else (movedIndex + delta - 1)

        val nodesToReNumber = mutableListOf<String>()
        var lowerBondOrder: String? = null
        var index = targetIndex
        while (index >= 0 && lowerBondOrder == null) {
            val node = orderedSpacesToOrderMap.getOrNull(index)
            if (node != null ) {
                val nodeOrder = node.second
                if (node.first == movedSpaceId) break
                if (nodeOrder == null) {
                    nodesToReNumber.add(0, node.first)
                } else {
                    lowerBondOrder = nodeOrder
                }
            }
            index--
        }
        nodesToReNumber.add(movedSpaceId)
        val afterSpace: Pair<String, String?>? = if (orderedSpacesToOrderMap.indices.contains(targetIndex + 1)) {
            orderedSpacesToOrderMap[targetIndex + 1]
        } else null

        val defaultMaxOrder = CharArray(4) { StringOrderUtils.DEFAULT_ALPHABET.last() }
                .joinToString("")

        val defaultMinOrder = CharArray(4) { StringOrderUtils.DEFAULT_ALPHABET.first() }
                .joinToString("")

        val afterOrder = afterSpace?.second ?: defaultMaxOrder

        val beforeOrder = lowerBondOrder ?: defaultMinOrder

        val newOrder = StringOrderUtils.midPoints(beforeOrder, afterOrder, nodesToReNumber.size)

        if (newOrder.isNullOrEmpty()) {
            
            val expectedList = orderedSpacesToOrderMap.toMutableList()
            expectedList.removeAt(movedIndex).let {
                expectedList.add(movedIndex + delta, it)
            }

            return StringOrderUtils.midPoints(defaultMinOrder, defaultMaxOrder, orderedSpacesToOrderMap.size)?.let { orders ->
                expectedList.mapIndexed { index, pair ->
                    SpaceReOrderCommand(
                            pair.first,
                            orders[index]
                    )
                }
            } ?: emptyList()
        } else {
            return nodesToReNumber.mapIndexed { i, s ->
                SpaceReOrderCommand(
                        s,
                        newOrder[i]
                )
            }
        }
    }
}
