package com.yazici.mongodb.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yazici.mongodb.practice.bind.ConfigModule;
import com.yazici.mongodb.practice.bind.MongoClientModule;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by keremyzc on 07/12/2015.
 */
public class MongoInsertTest {


    public static final String DATABASE_NAME = "kytestdb";
    public static final String COLLECTION_NAME = "users";
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
    public void shouldInsertASingleDocument() throws Exception {
        final MongoDatabase kytestdb = mongoClient.getDatabase("kytestdb");
        final MongoCollection<Document> usersCollection = kytestdb.getCollection("users");

        final Document user = new Document("name", "john")
                .append("age", 33)
                .append("phone", "0983042");

        usersCollection.insertOne(user);
    }

    @Test
    public void shouldInsertAnEmbeddedDocument() throws Exception {
        final MongoDatabase kytestdb = mongoClient.getDatabase("kytestdb");
        final MongoCollection<Document> usersCollection = kytestdb.getCollection("users");

        final Document user = new Document("name", "alberto")
                .append("age", 52)
                .append("info",
                        new Document("email", "alberto@gmail.com")
                                .append("phone", "9283742"));

        usersCollection.insertOne(user);
    }

    @Test
    public void shouldInsertAnArrayOfData() throws Exception {
        final MongoDatabase kytestdb = mongoClient.getDatabase(DATABASE_NAME);
        final MongoCollection<Document> usersCollection = kytestdb.getCollection(COLLECTION_NAME);

        final List<Document> kids = Arrays.asList(
                new Document("name", "hanna"),
                new Document("name", "emma")
        );

        final Document user = new Document("name", "alberto")
                .append("age", 52)
                .append("kids", kids)
                .append("info",
                        new Document("email", "alberto@gmail.com")
                                .append("phone", "9283742"));

        usersCollection.insertOne(user);
    }
}