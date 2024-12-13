package moe.protasis.sephirah.config;

import moe.protasis.sephirah.util.Converters;
import moe.protasis.sephirah.util.JsonWrapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "tomlynd";
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        adapter.registerConverter(new Converters.DateTimeReadConverter());
        adapter.registerConverter(new Converters.DateTimeWriteConverter());
        adapter.registerConverter(new JsonWrapper.JsonWrapperReadConverter());
        adapter.registerConverter(new JsonWrapper.JsonWrapperWriteConverter());
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
