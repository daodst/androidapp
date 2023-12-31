

package im.vector.app.core.date

import org.threeten.bp.format.DateTimeFormatter

interface DateFormatterProvider {

    val dateWithMonthFormatter: DateTimeFormatter

    val dateWithYearFormatter: DateTimeFormatter
}
