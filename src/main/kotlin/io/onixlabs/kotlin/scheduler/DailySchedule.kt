package io.onixlabs.kotlin.scheduler

import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.math.abs

/**
 * Represents a schedule of days and times of day.
 *
 * @param start The date and time when the schedule starts.
 * @param frequency The number of elapsed days per schedule cycle.
 * @param timesOfDay Times of day for each of the specified days.
 */
class DailySchedule(
    start: Instant,
    frequency: Long,
    private val timesOfDay: Set<LocalTime>
) : Schedule(start, frequency) {

    /**
     * Determines whether the schedule is satisfied by the candidate [Instant] value.
     *
     * @param candidate The candidate [Instant] which will be evaluated by the schedule.
     * @return Returns true if the schedule is satisfied by the candidate [Instant] value.
     */
    override fun isSatisfiedBy(candidate: Instant): Boolean {
        val elapsedDays = abs(Duration.between(candidate, start).toDays())

        return super.isSatisfiedBy(candidate)
                && elapsedDays % frequency == 0L
                && candidate.atZone(ZoneOffset.UTC).toLocalTime() in timesOfDay
    }
}