package de.smartsteuer.metricsdemo.tax

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
internal class TaxComputationControllerTest {
  @Autowired
  private lateinit var mvc: MockMvc
  @MockkBean(relaxed = true)
  private lateinit var cachingTaxComputer: CachingTaxComputer

  @Test
  fun `computeTaxRate returns json with sanitized taxable income and tax rate`() {
    every { cachingTaxComputer.computeTax(20_000) } returns TaxComputationResult(20_000, 2_346)
    mvc.get ("/computeTaxRate") {
      param("taxableIncome", "20000")
      accept = MediaType.APPLICATION_JSON
    }.andDo {
      print()
    }.andExpect {
      status { isOk() }
      content { contentTypeCompatibleWith (MediaType.APPLICATION_JSON) }
      jsonPath("$.taxableIncome") { value(20_000)}
      jsonPath("$.taxRate")       { value(2_346)}
      verify(exactly = 1) { cachingTaxComputer.computeTax(20_000) }
    }
  }

  @Test
  fun `computeTaxRate returns status 400 if taxableIncome query param is missing`() {
    mvc.get ("/computeTaxRate") {
      accept = MediaType.APPLICATION_JSON
    }.andDo {
      print()
    }.andExpect {
      status { is4xxClientError() }
      verify { cachingTaxComputer.computeTax(any()) wasNot Called  }
    }
  }

  @Test
  fun `computeTaxRate returns 400 if query param taxableInput is not a valid number`() {
    mvc.get ("/computeTaxRate") {
      accept = MediaType.APPLICATION_JSON
      param("taxableIncome", "not a number")
    }.andDo {
      print()
    }.andExpect {
      status { is4xxClientError() }
      verify { cachingTaxComputer.computeTax(any()) wasNot Called  }
    }
  }
}