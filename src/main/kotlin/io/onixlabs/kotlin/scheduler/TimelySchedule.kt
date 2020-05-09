package io.onixlabs.kotlin.scheduler

import io.onixlabs.kotlin.core.time.toLong
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Represents a schedule of an elapsed duration.
 *
 * @param start The date and time when the schedule starts.
 * @param duration The elapsed duration of the schedule.
 */
class TimelySchedule(
    start: Instant,
    duration: Duration,
    private val precision: TimeUnit = TimeUnit.MILLISECONDS
) : Schedule(start, duration.toLong(precision)) {

    /**
     * Determines whether the schedule is satisfied by the candidate [Instant] value.
     *
     * @param candidate The candidate [Instant] which will be evaluated by the schedule.
     * @return Returns true if the schedule is satisfied by the candidate [Instant] value.
     */
    override fun isSatisfiedBy(candidate: Instant): Boolean {
        val elapsed = abs(Duration.between(candidate, start).toLong(precision))
        return super.isSatisfiedBy(candidate) && elapsed % frequency == 0L
    }
}