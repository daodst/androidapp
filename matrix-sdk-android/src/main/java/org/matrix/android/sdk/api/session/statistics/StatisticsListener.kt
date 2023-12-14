

package org.matrix.android.sdk.api.session.statistics

import org.matrix.android.sdk.api.session.Session

interface StatisticsListener {
    fun onStatisticsEvent(session: Session, statisticEvent: StatisticEvent) = Unit
}
