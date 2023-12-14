

package im.vector.app.core.date

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import im.vector.app.core.resources.DateProvider
import im.vector.app.core.resources.LocaleProvider
import im.vector.app.core.resources.toTimestamp
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Period
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.absoluteValue

class VectorDateFormatter @Inject constructor(private val context: Context,
                                              private val localeProvider: LocaleProvider,
                                              private val dateFormatterProviders: DateFormatterProviders) {

    private val hourFormatter by lazy {
        if (DateFormat.is24HourFormat(context)) {
            DateTimeFormatter.ofPattern("HH:mm", localeProvider.current())
        } else {
            DateTimeFormatter.ofPattern("h:mm a", localeProvider.current())
        }
    }

    private val fullDateFormatter by lazy {
        val pattern = if (DateFormat.is24HourFormat(context)) {
            DateFormat.getBestDateTimePattern(localeProvider.current(), "EEE, d MMM yyyy HH:mm")
        } else {
            DateFormat.getBestDateTimePattern(localeProvider.current(), "EEE, d MMM yyyy h:mm a")
        }
        DateTimeFormatter.ofPattern(pattern, localeProvider.current())
    }

    
    fun format(ts: Long?, dateFormatKind: DateFormatKind): String {
        if (ts == null) return "-"
        val localDateTime = DateProvider.toLocalDateTime(ts)
        return when (dateFormatKind) {
            DateFormatKind.DEFAULT_DATE_AND_TIME -> formatDateAndTime(ts)
            DateFormatKind.ROOM_LIST             -> formatTimeOrDate(
                    date = localDateTime,
                    showTimeIfSameDay = true,
                    abbrev = true,
                    useRelative = true
            )
            DateFormatKind.TIMELINE_DAY_DIVIDER  -> formatTimeOrDate(
                    date = localDateTime,
                    alwaysShowYear = true
            )
            DateFormatKind.MESSAGE_DETAIL        -> formatFullDate(localDateTime)
            DateFormatKind.MESSAGE_SIMPLE        -> formatHour(localDateTime)
            DateFormatKind.EDIT_HISTORY_ROW      -> formatHour(localDateTime)
            DateFormatKind.EDIT_HISTORY_HEADER   -> formatTimeOrDate(
                    date = localDateTime,
                    abbrev = true,
                    useRelative = true
            )
        }
    }

    private fun formatFullDate(localDateTime: LocalDateTime): String {
        return fullDateFormatter.format(localDateTime)
    }

    private fun formatHour(localDateTime: LocalDateTime): String {
        return hourFormatter.format(localDateTime)
    }

    private fun formatDateWithMonth(localDateTime: LocalDateTime, abbrev: Boolean = false): String {
        return dateFormatterProviders.provide(abbrev).dateWithMonthFormatter.format(localDateTime)
    }

    private fun formatDateWithYear(localDateTime: LocalDateTime, abbrev: Boolean = false): String {
        return dateFormatterProviders.provide(abbrev).dateWithYearFormatter.format(localDateTime)
    }

    
    private fun formatTimeOrDate(
            date: LocalDateTime?,
            showTimeIfSameDay: Boolean = false,
            useRelative: Boolean = false,
            alwaysShowYear: Boolean = false,
            abbrev: Boolean = false
    ): String {
        if (date == null) {
            return ""
        }
        val currentDate = DateProvider.currentLocalDateTime()
        val isSameDay = date.toLocalDate() == currentDate.toLocalDate()
        return if (showTimeIfSameDay && isSameDay) {
            formatHour(date)
        } else {
            formatDate(date, currentDate, alwaysShowYear, abbrev, useRelative)
        }
    }

    private fun formatDate(
            date: LocalDateTime,
            currentDate: LocalDateTime,
            alwaysShowYear: Boolean,
            abbrev: Boolean,
            useRelative: Boolean
    ): String {
        val period = Period.between(date.toLocalDate(), currentDate.toLocalDate())
        return if (period.years.absoluteValue >= 1 || alwaysShowYear) {
            formatDateWithYear(date, abbrev)
        } else if (useRelative && period.days.absoluteValue < 2 && period.months.absoluteValue < 1) {
            getRelativeDay(date.toTimestamp())
        } else {
            formatDateWithMonth(date, abbrev)
        }
    }

    
    private fun formatDateAndTime(ts: Long): String {
        val date = DateProvider.toLocalDateTime(ts)
        val currentDate = DateProvider.currentLocalDateTime()
        
        
        val fakeDate = LocalDateTime.of(currentDate.toLocalDate(), date.toLocalTime())
        val formattedTime = DateUtils.getRelativeTimeSpanString(context, fakeDate.toTimestamp(), true).toString()
        val formattedDate = formatDate(date, currentDate, alwaysShowYear = false, abbrev = true, useRelative = true)
        return "$formattedDate $formattedTime"
    }

    
    private fun getRelativeDay(ts: Long): String {
        return DateUtils.getRelativeTimeSpanString(
                ts,
                System.currentTimeMillis(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_SHOW_WEEKDAY).toString()
    }
}
