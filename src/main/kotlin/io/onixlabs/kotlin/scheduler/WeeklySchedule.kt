package io.onixlabs.kotlin.scheduler

import java.time.*
import java.time.temporal.IsoFields
import kotlin.math.abs

/**
 * Represents a schedule of weeks, days and times of day.
 *
 * @param start The date and time when the schedule starts.
 * @param frequency The number of elapsed weeks between the schedule.
 * @param daysOfWeek The days of week of the schedule.
 * @param timesOfDay tht times of day of the schedule.
 */
class WeeklySchedule(
    start: Instant,
    frequency: Long,
    private val daysOfWeek: Set<DayOfWeek>,
    private val timesOfDay: Set<LocalTime>
) : Schedule(start, frequency) {

    /**
     * Determines whether the schedule is satisfied by the candidate [Instant] value.
     *
     * @param candidate The candidate [Instant] which will be evaluated by the schedule.
     * @return Returns true if the schedule is satisfied by the candidate [Instant] value.
     */
    override fun isSatisfiedBy(candidate: Instant): Boolean {

        fun getWeekOfYear(instant: Instant, zoneId: ZoneId = ZoneId.of("UTC")): Int {
            return instant
                .atZone(zoneId)
                .get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        }

        val elapsedWeeks = abs(getWeekOfYear(candidate) - getWeekOfYear(start))

        return super.isSatisfiedBy(candidate)
                && elapsedWeeks % frequency == 0L
                && DayOfWeek.from(candidate.atZone(ZoneOffset.UTC).toOffsetDateTime()) in daysOfWeek
                && candidate.atZone(ZoneOffset.UTC).toLocalTime() in timesOfDay
    }
}