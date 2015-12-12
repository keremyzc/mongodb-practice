package com.yazici.mongodb.practice.bind;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.yazici.mongodb.practice.domain.codec.UserCodec;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import javax.inject.Named;

/**
 * Created by keremyzc on 07/12/2015.
 */
public class MongoClientModule extends AbstractModule {


    @Override
    protected void configure() {
    }

    @Provides
    MongoClient provideMongoClient(@Named("com.yazici.mongo.host") String mongoHost,
                                   @Named("com.yazici.mongo.port") int mongoPort,
                                   MongoClientOptions options) {

        return new MongoClient(String.format("%s:%d", mongoHost, mongoPort), options);
    }

    @Provides
    MongoClientOptions provideMongoClient() {

        final CodecRegistry defaultCodecRegistry = MongoClient.getDefaultCodecRegistry();
        final Codec<Document> defaultDocumentCodec = defaultCodecRegistry.get(Document.class);

        final UserCodec userCodec = new UserCodec(defaultDocumentCodec);
        final CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                defaultCodecRegistry, CodecRegistries.fromCodecs(userCodec)
        );
        final MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry)
                .build();
        return options;
    }


}
