package de.smartsteuer.metricsdemo.metrics.fake

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
@Profile("generate-prometheus-test-data")
class MetricsEndpointRequestFilter(private val meterRegistry:               MeterRegistry,
                                   private val prometheusTestDataGenerator: PrometheusTestDataGenerator): Filter {

  override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
    if (isPrometheusMetricsEndpoint(request as HttpServletRequest)) {
      prometheusTestDataGenerator.generateNextState(meterRegistry)
    }
    chain.doFilter(request, response)
  }

  private fun isPrometheusMetricsEndpoint(request: HttpServletRequest): Boolean {
    return request.requestURI == "/actuator/prometheus"
  }
}
