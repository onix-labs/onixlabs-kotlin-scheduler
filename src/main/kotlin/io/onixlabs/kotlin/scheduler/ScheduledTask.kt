package io.onixlabs.kotlin.scheduler

import java.time.Instant

/**
 * Represents the base class for creating scheduled tasks.
 *
 * @param schedules The schedules against which the task is invoked.
 */
abstract class ScheduledTask(val schedules: MutableSet<Schedule>) {

    companion object {

        /**
         * Creates a scheduled task using a lambda function as its invocation target.
         *
         * @param schedules The schedules against which the task is invoked.
         * @return Returns a new [ScheduledTask] instance.
         */
        fun create(schedules: MutableSet<Schedule>, task: () -> Unit): ScheduledTask {
            return object : ScheduledTask(schedules) {
                override fun invoke() {
                    task()
                }
            }
        }
    }

    /**
     * Invokes the task if any of the schedules are satisfied by the specified [Instant].
     *
     * @param instant The instant against which the schedules are evaluated.
     */
    internal fun invoke(instant: Instant) {
        if (schedules.any { it.isSatisfiedBy(instant) }) {
            invoke()
        }
    }

    /**
     * Performs task invocation logic.
     */
    protected abstract fun invoke()
}