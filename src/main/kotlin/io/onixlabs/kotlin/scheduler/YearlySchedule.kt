package io.onixlabs.kotlin.scheduler

import io.onixlabs.kotlin.core.time.DayOfMonth
import java.time.*
import java.time.temporal.ChronoField
import kotlin.math.abs

/**
 * Represents a schedule of months, days and weeks or dates of the month and times of day.
 *
 * @param start The date and time when the schedule starts.
 * @param frequency The number of elapsed years per schedule cycle.
 * @param months The months in the year of the schedule.
 * @param daysOfMonth The month days and weeks of the schedule.
 * @param datesOfMonth The days of the month of the schedule.
 * @param timesOfDay Times of day for each of the specified years, months and days in the year
 */
class YearlySchedule(
    start: Instant,
    frequency: Long,
    private val months: Set<Month>,
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

        fun getYear(instant: Instant, zoneId: ZoneId = ZoneId.systemDefault()): Int {
            return instant
                .atZone(zoneId)
                .get(ChronoField.YEAR)
        }

        val elapsedYears = abs(getYear(candidate) - getYear(start))
        val candidateOffsetAtUTC = candidate.atZone(ZoneOffset.UTC).toLocalDateTime()

        return super.isSatisfiedBy(candidate)
                && elapsedYears % frequency == 0L
                && Month.from(candidateOffsetAtUTC) in months
                && (DayOfMonth.from(candidateOffsetAtUTC) in daysOfMonth
                || LocalDate.from(candidateOffsetAtUTC).dayOfMonth in datesOfMonth)
                && LocalTime.from(candidateOffsetAtUTC) in timesOfDay
    }
}