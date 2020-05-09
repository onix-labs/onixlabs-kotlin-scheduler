package io.onixlabs.kotlin.scheduler

import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

/**
 * Represents a high precision, multi-threaded task scheduler.
 *
 * @param threadPoolSize Determines the number of threads in the scheduler service. The default value is 1.
 * @param interval The interval of the scheduler service. The default value is 1.
 * @param precision The precision of the scheduler service. This default is MILLISECONDS.
 */
class TaskScheduler(
    private val threadPoolSize: Int = 1,
    private val interval: Long = 1,
    private val precision: TimeUnit = TimeUnit.MILLISECONDS
) {

    /**
     * A mutable map of named, scheduled tasks.
     */
    private val scheduledTasks = mutableMapOf<String, ScheduledTask>()

    /**
     * The underlying scheduler service.
     */
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(threadPoolSize)

    /**
     * The proxy task executed by the scheduler service.
     * This calls invoke on each of the scheduled tasks in parallel.
     */
    private val task = timerTask {
        scheduledTasks.values.parallelStream().forEach {
            it.invoke(Instant.now())
        }
    }

    /**
     * Adds a named, scheduled task to the scheduler.
     *
     * @param name The name of the scheduled task.
     * @param schedules The schedules against which the task will execute.
     * @param task The task to execute.
     */
    @Suppress
    fun addScheduledTask(name: String, schedules: Set<Schedule>, task: () -> Unit) {

        if (scheduledTasks.containsKey(name)) {
            throw IllegalStateException("Task with name '$name' already exists.")
        }

        scheduledTasks[name] = ScheduledTask.create(schedules.toMutableSet(), task)
    }

    /**
     * Adds a named, scheduled task to the scheduler.
     *
     * @param name The name of the scheduled task.
     * @param schedule The schedule against which the task will execute.
     * @param task The task to execute.
     */
    fun addScheduledTask(name: String, schedule: Schedule, task: () -> Unit) {
        addScheduledTask(name, mutableSetOf(schedule), task)
    }

    /**
     * Removes a scheduled task from the scheduler.
     *
     * @param name The name of the scheduled task.
     * @throws IllegalStateException if the named, scheduled task does not exist.
     */
    fun removeScheduledTask(name: String) {

        if (!scheduledTasks.containsKey(name)) {
            throw IllegalStateException("Task with name '$name' does not exists.")
        }

        scheduledTasks.remove(name)
    }

    /**
     * Adds a new schedule to an existing named, scheduled task.
     *
     * @param name The name of the scheduled task.
     * @param schedule The scheduled to add to an existing named, scheduled task.
     * @throws IllegalStateException if the named, scheduled task does not exist.
     */
    fun addSchedule(name: String, schedule: Schedule) {

        if (!scheduledTasks.containsKey(name)) {
            throw IllegalStateException("Task with name '$name' does not exists.")
        }

        scheduledTasks[name]!!.schedules.add(schedule)
    }

    /**
     * Removes a schedule from an existing named, scheduled task.
     *
     * @param name The name of the scheduled task.
     * @param schedule The scheduled to remove from an existing named, scheduled task.
     * @throws IllegalStateException if the named, scheduled task does not exist.
     */
    fun removeSchedule(name: String, schedule: Schedule) {

        if (!scheduledTasks.containsKey(name)) {
            throw IllegalStateException("Task with name '$name' does not exists.")
        }

        scheduledTasks[name]!!.schedules.remove(schedule)
    }

    /**
     * Starts the scheduler service.
     */
    fun start() {
        executor.scheduleAtFixedRate(task, 0, interval, precision)
    }

    /**
     * Stops the scheduler service.
     */
    fun stop() {
        executor.shutdown()
    }
}