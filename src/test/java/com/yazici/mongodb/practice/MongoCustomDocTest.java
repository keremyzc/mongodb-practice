package com.yazici.mongodb.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yazici.mongodb.practice.bind.ConfigModule;
import com.yazici.mongodb.practice.bind.MongoClientModule;
import com.yazici.mongodb.practice.domain.User;
import com.yazici.mongodb.practice.domain.codec.UserCodec;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by keremyzc on 12/12/2015.
 */
public class MongoCustomDocTest {
    public static final String DATABASE_NAME = "kytestdb";
    public static final String COLLECTION_NAME = "users";
    private static Injector injector;
    private static MongoClient mongoClient;
    private static MongoCollection<Document> usersCollection;

    @BeforeClass
    public static void setUpClass() throws Exception {
        injector = Guice.createInjector(new ConfigModule(), new MongoClientModule());

        mongoClient = injector.getInstance(MongoClient.class);
        final MongoDatabase kytestdb = mongoClient.getDatabase(DATABASE_NAME);
        usersCollection = kytestdb.getCollection(COLLECTION_NAME);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        usersCollection.drop();
        mongoClient.close();
        injector = null;
    }


    @Test
    public void shouldInsertASingleDocumentWithCodec() throws Exception {

        final User john = new User("john", 32);

        usersCollection.insertOne(john);

        final User actualUser = usersCollection.find(john, User.class).first();

        assertThat("it should have inserted a single user domain object " + john, actualUser, is(equalTo(john)));
    }

}
