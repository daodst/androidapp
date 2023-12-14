

package im.vector.app.test.fixtures

import im.vector.app.features.analytics.plan.UserProperties
import im.vector.app.features.analytics.plan.UserProperties.FtueUseCaseSelection

fun aUserProperties(
        ftueUseCase: FtueUseCaseSelection? = FtueUseCaseSelection.Skip
) = UserProperties(
        ftueUseCaseSelection = ftueUseCase
)
