package de.smartsteuer.metricsdemo

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object RandomIncome {
  private class BezierFunction(p0: Double, p1: Double, p2: Double, p3: Double) {
    private val b0 = p0
    private val b1 = 3 * (p1 - p0)
    private val b2 = 3 * (p2 - 2 * p1 + p0)
    private val b3 = p3 - 3 * p2 + 3 * p1 - p0
    def evaluate(t: Double): Double = b0 + t * (b1 + t * (b2 + t * b3))
  }

  private def createNormalizedDistributionFunction(): Double => Double = {
    val leftFunction  = new BezierFunction(0.0, 0.0, 1.0, 1.0)
    val rightFunction = new BezierFunction(1.0, 1.0, 0.0, 0.0)
    (x: Double) => if (x < 0.5) leftFunction.evaluate(x * 2.0) else rightFunction.evaluate((x - 0.5) * 2.0)
  }

  private def createNormalizedRandomFunction(): () => Double = {
    val distributionFunction = createNormalizedDistributionFunction()
    val buckets = ArrayBuffer[Double]()
    val rounds  = 400
    val factor  =  25
    for(round <- 0 until rounds) {
      val input = round.toDouble / rounds
      val bucketCount = (distributionFunction(input) * factor).toInt
      for(_ <- 0 until bucketCount) {
        buckets += input
      }
    }
    () => buckets(Random.nextInt(buckets.size))
  }

  private val scale         = 1_000.0
  private val minimumIncome = 5_000.0
  private val maximumIncome = 200_000.0
  private def normalizedRandomFunction: () => Double = createNormalizedRandomFunction()
  private def randomIncomeFunction(): Int = (((maximumIncome - minimumIncome) / scale * normalizedRandomFunction()).toInt * scale + minimumIncome).toInt

  val feeder: Iterator[Map[String, Int]] =
    Iterator.continually(Map("income" -> randomIncomeFunction()))
}
