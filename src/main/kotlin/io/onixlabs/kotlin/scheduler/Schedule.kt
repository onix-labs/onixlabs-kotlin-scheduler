package io.onixlabs.kotlin.scheduler

import java.time.Instant

/**
 * Represents the base class for types of schedule
 *
 * @param start The date and time when the schedule starts.
 * @param frequency An abstract value representing the frequency, or rate of occurrence of the schedule.
 */
abstract class Schedule(protected val start: Instant, protected val frequency: Long) {

    /**
     * Determines whether the schedule is satisfied by the candidate [Instant] value.
     *
     * @param candidate The candidate [Instant] which will be evaluated by the schedule.
     * @return Returns true if the schedule is satisfied by the candidate [Instant] value.
     */
    open fun isSatisfiedBy(candidate: Instant): Boolean {
        return candidate >= start
    }
}