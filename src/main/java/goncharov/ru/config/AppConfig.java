package goncharov.ru.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class AppConfig {
    /**
     * Convert to a string accurate to the day.
     *
     * @return
     */
    @Bean("date_bean")
    public SimpleDateFormat simpleDateFormatForDate() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Convert to a string accurate to the hour.
     *
     * @return
     */
    @Bean("time_bean")
    public SimpleDateFormat simpleDateFormatForTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH");
    }
}
