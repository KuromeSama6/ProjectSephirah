package moe.protasis.sephirah.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.Date;

public class Converters {
    @ReadingConverter
    public static class DateTimeReadConverter implements Converter<Date, DateTime> {
        @Override
        public DateTime convert(Date source) {
            return new DateTime(source);
        }
    }

    @WritingConverter
    public static class DateTimeWriteConverter implements Converter<DateTime, Date> {
        @Override
        public Date convert(DateTime source) {
            return source.toDate();
        }
    }

}
