package de.smartsteuer.metricsdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MetricsDemoApplication

fun main(args: Array<String>) {
  runApplication<MetricsDemoApplication>(*args)
}
