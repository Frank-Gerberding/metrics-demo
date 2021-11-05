package de.smartsteuer.metricsdemo.tax

import com.google.common.cache.LoadingCache
import com.ninjasquad.springmockk.MockkBean
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
internal class CacheControllerTest {
  @Autowired
  private lateinit var mvc: MockMvc
  @MockkBean(relaxed = true)
  private lateinit var taxComputationCache: LoadingCache<Euro, TaxComputationResult>

  @Test
  fun `cache-clear returns always status 200`() {
    mvc.post ("/cache/clear") {
    }.andDo {
      print()
    }.andExpect {
      status { isOk() }
      verify(exactly = 1) { taxComputationCache.invalidateAll() }
    }
  }
}