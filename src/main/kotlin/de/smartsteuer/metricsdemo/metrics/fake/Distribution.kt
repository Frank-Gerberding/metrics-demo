package de.smartsteuer.metricsdemo.metrics.fake

import kotlin.random.Random

typealias DistributionFunction = () -> Double

private class BezierFunction(p0: Double, p1: Double, p2: Double, p3: Double) {
  private val b0 = p0
  private val b1 = 3 * (p1 - p0)
  private val b2 = 3 * (p2 - 2 * p1 + p0)
  private val b3 = p3 - 3 * p2 + 3 * p1 - p0
  fun evaluate(t: Double) = b0 + t * (b1 + t * (b2 + t * b3))
}

private fun createNormalizedDistributionFunction(): (Double) -> Double {
  val leftFunction  = BezierFunction(0.0, 0.0, 1.0, 1.0)
  val rightFunction = BezierFunction(1.0, 1.0, 0.0, 0.0)
  return fun(x: Double) = if (x < 0.5) leftFunction.evaluate(x * 2.0) else rightFunction.evaluate((x - 0.5) * 2.0)
}

private fun createNormalizedRandomFunction(): DistributionFunction {
  val distributionFunction = createNormalizedDistributionFunction()
  val buckets = mutableListOf<Double>()
  val rounds  = 400
  val factor  =  25
  repeat(rounds) {
    val input = it.toDouble() / rounds
    val bucketCount = ((distributionFunction(input)) * factor).toInt()
    repeat(bucketCount) {
      buckets += input
    }
  }
  val random = Random.Default
  return fun(): Double {
    val randomValue = random.nextInt(buckets.size)
    return buckets[randomValue]
  }
}

private val normalizedRandomFunction = createNormalizedRandomFunction()

enum class DistributionElementType { ZERO, NORMAL_DISTRIBUTION }

data class DistributionElement(val type: DistributionElementType, val length: Double, val offset: Double = 0.0) {
  fun scale(factor: Double)     = DistributionElement(type, length * factor, offset * factor)
  fun translate(offset: Double) = DistributionElement(type, length, offset)
}

fun createCompositeDistributionFunction(vararg elements: DistributionElement): DistributionFunction {
  val elementLengthSum             = elements.map { it.length }.sum()
  val normalizedElements           = elements.map { it.scale(1.0 / elementLengthSum) }
  val normalizedElementsWithOffset = addOffset(normalizedElements)
  val nonZeroElements              = normalizedElementsWithOffset.filter { it.type != DistributionElementType.ZERO }
  val nonZeroElementLengthSum      = nonZeroElements.map { it.length }.sum()
  val random                       = Random.Default

  fun findElement(input: Double): DistributionElement {
    var cumulativeLength = 0.0
    nonZeroElements.forEach { element ->
      if (input <= cumulativeLength + element.length) return element
      cumulativeLength += element.length
    }
    return nonZeroElements.first()
  }
  return fun(): Double {
    val input   = random.nextDouble(nonZeroElementLengthSum)
    val element = findElement(input)
    return element.offset + element.length * normalizedRandomFunction()
  }
}

private fun addOffset(elements: List<DistributionElement>): List<DistributionElement> {
  var cumulativeLength = 0.0
  val result = mutableListOf<DistributionElement>()
  elements.forEach { element ->
    result += element.translate(cumulativeLength)
    cumulativeLength += element.length
  }
  return result
}
