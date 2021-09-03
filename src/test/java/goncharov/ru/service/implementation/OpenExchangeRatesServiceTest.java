package goncharov.ru.service.implementation;

import goncharov.ru.client.FeignOpenExchangeRatesClient;
import goncharov.ru.model.ExchangeRates;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan("goncharov.ru")
public class OpenExchangeRatesServiceTest {
    @Value("${openexchangerates.base}")
    private String base;
    @Autowired
    private OpenExchangeRatesService exchangeRatesService;
    @MockBean
    private FeignOpenExchangeRatesClient openExchangeRatesClient;

    private ExchangeRates currentRates;
    private ExchangeRates prevRates;

    /**
     * T1 - doesn't change.
     * T2 - increases.
     * T3 - decreases.
     */
    @Before
    public void init() {
        int time = 1609459199;
        this.currentRates = new ExchangeRates();
        this.currentRates.setTimestamp(time);
        this.currentRates.setBase("T_BASE");
        Map<String, Double> currentRatesMap = new HashMap<>();
        currentRatesMap.put("T1", 0.1);
        currentRatesMap.put("T2", 0.5);
        currentRatesMap.put("T3", 1.0);
        currentRatesMap.put(this.base, 73.108);
        currentRatesMap.put("T_BASE", 1.0);
        this.currentRates.setRates(currentRatesMap);

        time = 1609372799;
        this.prevRates = new ExchangeRates();
        this.prevRates.setTimestamp(time);
        this.prevRates.setBase("T_BASE");
        Map<String, Double> prevRatesMap = new HashMap<>();
        prevRatesMap.put("T1", 0.1);
        prevRatesMap.put("T2", 1.0);
        prevRatesMap.put("T3", 0.5);
        prevRatesMap.put(this.base, 73.108);
        prevRatesMap.put("T_BASE", 1.0);
        this.prevRates.setRates(prevRatesMap);

    }

    @Test
    public void whenPositiveChanges() {
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        int result = exchangeRatesService.getKeyForTag("T3");
        assertEquals(1, result);
    }

    @Test
    public void whenNegativeChanges() {
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        int result = exchangeRatesService.getKeyForTag("T2");
        assertEquals(-1, result);
    }

    @Test
    public void whenZeroChanges() {
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        int result = exchangeRatesService.getKeyForTag("T1");
        assertEquals(0, result);
    }

    @Test
    public void whenGotNullOnInput() {
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        int result = exchangeRatesService.getKeyForTag(null);
        assertEquals(-101, result);
    }

    @Test
    public void whenGotUnExistCharCode() {
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        int result = exchangeRatesService.getKeyForTag("UNEXISTED");
        assertEquals(-101, result);
    }

    @Test
    public void whenGetList() {
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        List<String> result = exchangeRatesService.getCharCodes();
        assertThat(result, containsInAnyOrder("T1", "T2", "T3", base, "T_BASE"));
    }

    @Test
    public void whenCurrentIsNull() {
        this.currentRates = null;
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        int result = exchangeRatesService.getKeyForTag("T1");
        assertEquals(-101, result);
    }

    @Test
    public void whenPrevIsNull() {
        this.prevRates = null;
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        int result = exchangeRatesService.getKeyForTag("T1");
        assertEquals(-101, result);
    }

    @Test
    public void whenCurrentAndPrevIsNull() {
        this.currentRates = null;
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        int result = exchangeRatesService.getKeyForTag("T1");
        assertEquals(-101, result);
    }

    @Test
    public void whenAppBaseIsChanged() {
        this.currentRates.getRates().put(this.base, 73.945);
        Mockito.when(openExchangeRatesClient.getLatestRates(anyString()))
                .thenReturn(this.currentRates);
        Mockito.when(openExchangeRatesClient.getHistoricalRates(anyString(), anyString()))
                .thenReturn(this.prevRates);
        int result = exchangeRatesService.getKeyForTag(this.base);
        assertEquals(0, result);
    }
}
