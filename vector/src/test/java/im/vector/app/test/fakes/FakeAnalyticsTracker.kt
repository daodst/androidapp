

package im.vector.app.test.fakes

import im.vector.app.features.analytics.AnalyticsTracker
import io.mockk.mockk

class FakeAnalyticsTracker : AnalyticsTracker by mockk()
