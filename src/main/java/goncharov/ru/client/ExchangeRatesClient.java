package goncharov.ru.client;

import goncharov.ru.model.ExchangeRates;

public interface ExchangeRatesClient {

    ExchangeRates getLatestRates(String appId);

    ExchangeRates getHistoricalRates(String date, String appId);
}
