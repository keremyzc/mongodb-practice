package com.yazici.mongodb.practice.bind;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mongodb.MongoClient;

import javax.inject.Named;

/**
 * Created by keremyzc on 07/12/2015.
 */
public class MongoClientModule extends AbstractModule {


    @Override
    protected void configure() {}

    @Provides
    MongoClient provideMongoClient(@Named("com.yazici.mongo.host") String mongoHost,
                                   @Named("com.yazici.mongo.port") int mongoPort) {
        return new MongoClient(mongoHost, mongoPort);
    }
}
