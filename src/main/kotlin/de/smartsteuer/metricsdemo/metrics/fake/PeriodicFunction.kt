@file:Suppress("unused")

package de.smartsteuer.metricsdemo.metrics.fake

import java.time.Duration
import java.util.*
import kotlin.math.PI
import kotlin.math.roundToLong
import kotlin.math.sin

typealias PeriodicFunction = (time: Long) -> Long

private val random = Random()

fun periodicFunction(period:          Duration,
                     valueOffset:     Long,
                     timeOffset:      Long,
                     amplitude:       Long,
                     jitterAmplitude: Double): PeriodicFunction {
  val periodMillis = period.toMillis()
  var error = 0.0
  return { time ->
    val doubleResult = (sin((time + timeOffset) * (2 * PI) / periodMillis) * amplitude + valueOffset).toInt() +
                       (jitterAmplitude * random.nextGaussian()) + error
    val intResult = doubleResult.roundToLong()
    error = doubleResult - intResult
    intResult
  }
}

fun normalizedDistributionFunction(x: Double): Double = (sin(x * 2 * PI - 0.5 * PI) + 1) / 2

fun distributionFunction(minValue: Double, maxValue: Double): () -> Double {
  val limit  = 3.0
  val factor = 1.0 / (2.0 * limit) * (maxValue - minValue)
  return { (random.nextGaussian().coerceAtLeast(-limit).coerceAtMost(limit) + limit) * factor + minValue }
}

fun distributionFunction(minValue: Duration, maxValue: Duration): () -> Duration {
  val doubleMinValue: Double = minValue.toMillis().toDouble()
  val doubleMaxValue: Double = maxValue.toMillis().toDouble()
  val doubleFunction = distributionFunction(doubleMinValue, doubleMaxValue)
  return { Duration.ofMillis(doubleFunction().toLong()) }
}

fun noMain() {
  val f = distributionFunction(10.seconds, 20.seconds)
  val counts = IntArray(21)
  repeat(100_000) {
    counts[f().toSeconds().toInt()]++
  }
  (10..20).forEach { println ("$it -> ${counts[it]}") }
}

data class CountPerDuration(val count: Int, val duration: Duration)

val Int.perMinute get() = CountPerDuration(this, Duration.ofMinutes(1))
val Int.perDay    get() = CountPerDuration(this, Duration.ofDays(1))
val Int.perMonth  get() = CountPerDuration(this, Duration.ofDays(30))

val Int.millis:  Duration get() = Duration.ofMillis(this.toLong())
val Int.seconds: Duration get() = Duration.ofSeconds(this.toLong())
val Int.minutes: Duration get() = Duration.ofMinutes(this.toLong())
val Int.hours:   Duration get() = Duration.ofHours(this.toLong())
val Int.days:    Duration get() = Duration.ofDays(this.toLong())

class PeriodicFunctionBuilder(private val period:         Duration,
                              private val jitterFactor:   Double   = 0.15,
                              private val scrapeInterval: Duration = Duration.ofSeconds(30)) {

  fun build(minimumCount: CountPerDuration, maximumCount: CountPerDuration): PeriodicFunction {
    val minimumFactor   = scrapeInterval.toMillis().toDouble() / minimumCount.duration.toMillis().toDouble()
    val maximumFactor   = scrapeInterval.toMillis().toDouble() / maximumCount.duration.toMillis().toDouble()
    val minimumValue    = (minimumCount.count * minimumFactor).toLong()
    val maximumValue    = (maximumCount.count * maximumFactor).toLong()
    val amplitude       = (maximumValue - minimumValue) / 2
    val valueOffset     = minimumValue + amplitude
    val timeOffset      = 0L
    val jitterAmplitude = amplitude * jitterFactor
    return periodicFunction(period, valueOffset, timeOffset, amplitude, jitterAmplitude)
  }

  fun build(minimumTime: Duration, maximumTime: Duration): PeriodicFunction {
    val minimumValue    = minimumTime.toMillis()
    val maximumValue    = maximumTime.toMillis()
    val amplitude       = (maximumValue - minimumValue) / 2
    val valueOffset     = minimumValue + amplitude
    val timeOffset      = 0L
    val jitterAmplitude = amplitude * jitterFactor
    return periodicFunction(period, valueOffset, timeOffset, amplitude, jitterAmplitude)
  }
}
