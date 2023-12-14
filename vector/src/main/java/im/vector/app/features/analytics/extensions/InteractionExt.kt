

package im.vector.app.features.analytics.extensions

import im.vector.app.features.analytics.plan.Interaction

fun Interaction.Name.toAnalyticsInteraction(interactionType: Interaction.InteractionType = Interaction.InteractionType.Touch) =
        Interaction(
                name = this,
                interactionType = interactionType
        )
