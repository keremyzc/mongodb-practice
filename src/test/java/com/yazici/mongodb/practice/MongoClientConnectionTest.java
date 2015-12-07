package com.yazici.mongodb.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yazici.mongodb.practice.bind.ConfigModule;
import com.yazici.mongodb.practice.bind.MongoClientModule;
import org.bson.Document;
import org.junit.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by keremyzc on 07/12/2015.
 */
public class MongoClientConnectionTest {


    private static Injector injector;
    private static MongoClient mongoClient;

    @BeforeClass
    public static void setUpClass() throws Exception {
        injector = Guice.createInjector(new ConfigModule(), new MongoClientModule());
        mongoClient = injector.getInstance(MongoClient.class);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        mongoClient.close();
        injector = null;
    }

    @Test
    public void shouldConnectToTheLocalMongoDB() throws Exception {
        assertThat("client connection couldn't be made", mongoClient, is(notNullValue()));
    }

    @Test
    public void shouldGetTheTestDBAndCollection() throws Exception {
        final MongoDatabase kytestdb = mongoClient.getDatabase("kytestdb");
        assertThat("couldn't get the db from mongodb", kytestdb, is(notNullValue()));

        final MongoCollection<Document> usersCollection = kytestdb.getCollection("users");
        assertThat("couldn't get the users collection from mongodb", usersCollection, is(notNullValue()));
    }
}