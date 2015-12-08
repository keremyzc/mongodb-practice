package com.yazici.mongodb.practice;

import com.google.common.collect.FluentIterable;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yazici.mongodb.practice.bind.ConfigModule;
import com.yazici.mongodb.practice.bind.MongoClientModule;
import org.bson.Document;
import org.junit.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

/**
 * Created by keremyzc on 08/12/2015.
 */
public class MongoQueryTest {


    public static final String DATABASE_NAME = "kytestdb";
    public static final String COLLECTION_NAME = "users";
    private static Injector injector;
    private static MongoClient mongoClient;
    private static MongoCollection<Document> usersCollection;
    private Document testUser1;
    private Document testUser2;

    @BeforeClass
    public static void setUpClass() throws Exception {
        injector = Guice.createInjector(new ConfigModule(), new MongoClientModule());
        mongoClient = injector.getInstance(MongoClient.class);
        final MongoDatabase kytestdb = mongoClient.getDatabase(DATABASE_NAME);
        usersCollection = kytestdb.getCollection(COLLECTION_NAME);

    }


    @Before
    public void insertSomeTestData() {
        testUser1 = new Document("name", "john")
                .append("age", 33)
                .append("phone", "0983042");

        testUser2 = new Document("name", "mario")
                .append("age", 46)
                .append("phone", "9238742");

        usersCollection.insertMany(Arrays.asList(testUser1, testUser2));
    }

    @After
    public void deleteTestData(){
        usersCollection.deleteOne(new Document("name", "john"));
        usersCollection.deleteOne(new Document("name", "mario"));
    }


    @AfterClass
    public static void tearDownClass() throws Exception {
        mongoClient.close();
        injector = null;
    }


    @Test
    public void shouldInsertASingleDocument() throws Exception {
        final FindIterable<Document> cursor = usersCollection.find();
        final List<Document> docs = FluentIterable.from(cursor).toList();

        assertThat(docs, hasItems(testUser1, testUser2));
    }

}