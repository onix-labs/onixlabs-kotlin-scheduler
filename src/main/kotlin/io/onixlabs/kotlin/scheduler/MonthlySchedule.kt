package io.onixlabs.kotlin.scheduler

import io.onixlabs.kotlin.core.time.DayOfMonth
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoField
import kotlin.math.abs

/**
 * Represents a schedule of months, days and weeks or dates of the month and times of day.
 *
 * @param start The date and time when the schedule starts.
 * @param frequency The number of elapsed months per schedule cycle.
 * @param daysOfMonth Specific days of the month.
 * @param datesOfMonth Specific dates in the month.
 * @param timesOfDay Times of day for each of the specified days and dates in the month.
 */
class MonthlySchedule(
    start: Instant,
    frequency: Long,
    private val daysOfMonth: Set<DayOfMonth>,
    private val datesOfMonth: Set<Int>,
    private val timesOfDay: Set<LocalTime>
) : Schedule(start, frequency) {

    /**
     * Determines whether the schedule is satisfied by the candidate [Instant] value.
     *
     * @param candidate The candidate [Instant] which will be evaluated by the schedule.
     * @return Returns true if the schedule is satisfied by the candidate [Instant] value.
     */
    override fun isSatisfiedBy(candidate: Instant): Boolean {

        fun getMonthOfYear(instant: Instant, zoneId: ZoneId = ZoneId.systemDefault()): Int {
            return instant
                .atZone(zoneId)
                .get(ChronoField.MONTH_OF_YEAR)
        }

        val elapsedMonths = abs(getMonthOfYear(candidate) - getMonthOfYear(start))

        return super.isSatisfiedBy(candidate)
                && elapsedMonths % frequency == 0L
                && (DayOfMonth.from(candidate.atZone(ZoneOffset.UTC).toLocalDate()) in daysOfMonth
                || candidate.atZone(ZoneOffset.UTC).toLocalDate().dayOfMonth in datesOfMonth)
                && candidate.atZone(ZoneOffset.UTC).toLocalTime() in timesOfDay
    }
}