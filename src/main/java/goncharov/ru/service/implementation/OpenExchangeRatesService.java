package goncharov.ru.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import goncharov.ru.client.ExchangeRatesClient;
import goncharov.ru.model.ExchangeRates;
import goncharov.ru.service.ExchangeRatesService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class OpenExchangeRatesService implements ExchangeRatesService {
    private ExchangeRates prevRates;
    private ExchangeRates currentRates;

    private ExchangeRatesClient exchangeRatesClient;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    @Value("${openexchangerates.app.id}")
    private String appId;
    @Value("${openexchangerates.base}")
    private String base;

    @Autowired
    public OpenExchangeRatesService(
            ExchangeRatesClient exchangeRatesClient,
            @Qualifier("date_bean") SimpleDateFormat dateFormat,
            @Qualifier("time_bean") SimpleDateFormat timeFormat
    ) {
        this.exchangeRatesClient = exchangeRatesClient;
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
    }

    /**
     * Returns list of available currencies.
     * @return
     */
    @Override
    public List<String> getCharCodes() {
        List<String> result = null;
        if (this.currentRates.getRates() != null) {
            result = new ArrayList<>(this.currentRates.getRates().keySet());
        }
        return result;
    }

    /**
     * Check/update exchange rates,
     * returns result of coefficients comparison.
     * Returns -101 if there are no rates or coefficients.
     * @param charCode
     * @return
     */
    @Override
    public int getKeyForTag(String charCode) {
        this.refreshRates();
        Double prevCoef = this.getCoefficient(this.prevRates, charCode);
        Double currentCoef = this.getCoefficient(this.currentRates, charCode);
        return prevCoef != null && currentCoef != null
                ? Double.compare(currentCoef, prevCoef)
                : -101;
    }

    /**
     * Check/update exchange rates
     */
    @Override
    public void refreshRates() {
        long currentTime = System.currentTimeMillis();
        this.refreshCurrentRates(currentTime);
        this.refreshPrevRates(currentTime);
    }

    /**
     * Check/update current exchange rates.
     * Rates on openexchangerates.org are updated every hour.
     * @param time
     */
    private void refreshCurrentRates(long time) {
        if (
                this.currentRates == null ||
                        !timeFormat.format(Long.valueOf(this.currentRates.getTimestamp()) * 1000)
                                .equals(timeFormat.format(time))
        ) {
            this.currentRates = exchangeRatesClient.getLatestRates(this.appId);
        }
    }

    /**
     * Check/update previous exchange rates.
     * @param time
     */
    private void refreshPrevRates(long time) {
        Calendar prevCalendar = Calendar.getInstance();
        prevCalendar.setTimeInMillis(time);
        String currentDate = dateFormat.format(prevCalendar.getTime());
        prevCalendar.add(Calendar.DAY_OF_YEAR, -1);
        String newPrevDate = dateFormat.format(prevCalendar.getTime());
        if (
                this.prevRates == null
                        || (
                        !dateFormat.format(Long.valueOf(this.prevRates.getTimestamp()) * 1000)
                                .equals(newPrevDate)
                                && !dateFormat.format(Long.valueOf(this.prevRates.getTimestamp()) * 1000)
                                .equals(currentDate)
                )
        ) {
            this.prevRates = exchangeRatesClient.getHistoricalRates(newPrevDate, appId);
        }
    }

    /**
     * The formula for calculating the coefficient in relation to the currency base
     * set in this application:
     * (Default_Base / Our_Base) * Target
     * @param rates
     * @param charCode
     */
    private Double getCoefficient(ExchangeRates rates, String charCode) {
        Double result = null;
        Double targetRate = null;
        Double appBaseRate = null;
        Double defaultBaseRate = null;
        Map<String, Double> map = null;
        if (rates != null && rates.getRates() != null) {
            map = rates.getRates();
            targetRate = map.get(charCode);
            appBaseRate = map.get(this.base);
            defaultBaseRate = map.get(rates.getBase());
        }
        if (targetRate != null && appBaseRate != null && defaultBaseRate != null) {
            result = new BigDecimal(
                    (defaultBaseRate / appBaseRate) * targetRate
            )
                    .setScale(4, RoundingMode.UP)
                    .doubleValue();
        }
        return result;
    }
}
