package moe.protasis.sephirah.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import moe.protasis.sephirah.util.Converters;
import moe.protasis.sephirah.util.JsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.uri}")
    private String uri;
    @Value("${spring.data.mongodb.database}")
    private String db;

    @Override
    protected String getDatabaseName() {
        return db;
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

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(uri);
    }

    @Bean
    public GridFsTemplate gridFsTemplate(MappingMongoConverter mongoConverter) throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mongoConverter);
    }

}
