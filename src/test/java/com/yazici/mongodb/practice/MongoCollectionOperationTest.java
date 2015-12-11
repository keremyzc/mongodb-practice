package com.yazici.mongodb.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.result.DeleteResult;
import com.yazici.mongodb.practice.bind.ConfigModule;
import com.yazici.mongodb.practice.bind.MongoClientModule;
import org.bson.Document;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by keremyzc on 08/12/2015.
 */
public class MongoCollectionOperationTest {


    public static final String DATABASE_NAME = "kytestdb";
    public static final String COLLECTION_NAME = "testCollection";
    public static final String COLLECTION_NAME2 = "testCollection2";
    private static final long SINGLEL = 1L;
    public static final int MB = 1024 * 1024;

    private static Injector injector;
    private static MongoClient mongoClient;
    private static MongoDatabase kytestdb;
    private Document testUser1;

    @BeforeClass
    public static void setUpClass() throws Exception {
        injector = Guice.createInjector(new ConfigModule(), new MongoClientModule());
        mongoClient = injector.getInstance(MongoClient.class);
        kytestdb = mongoClient.getDatabase(DATABASE_NAME);

    }


    @Before
    public void insertSomeTestData() {
        testUser1 = new Document("name", "john")
                .append("age", 33)
                .append("phone", "0983042");
    }

    @After
    public void deleteTestData() {
        kytestdb.getCollection(COLLECTION_NAME).drop();
        kytestdb.getCollection(COLLECTION_NAME2).drop();
    }


    @AfterClass
    public static void tearDownClass() throws Exception {
        mongoClient.close();
        injector = null;
    }

    @Test
    public void shouldCreateANewCollectionByInsert() throws Exception {
        final MongoCollection<Document> collection = kytestdb.getCollection(COLLECTION_NAME);

        collection.insertOne(testUser1);

        final long numberOfUesrs = collection.count();
        assertThat("should contain only a single user", numberOfUesrs, is(equalTo(SINGLEL)));

        final Document actualUser = collection.find().first();
        assertThat("test user 1 should have been inserted", actualUser, is(equalTo(testUser1)));


    }

    /**
     * A Capped collection is a fixed-size collection, which supports high-throughput insert/retrieve operations based on insertion order.
     * A capped collection acts much the same as a circular buffer.
     * As a collection fills out its allocated space, it overwrites the oldest documents to make room.
     */
    @Test
    public void shouldCreateACappedCollection() {
        final CreateCollectionOptions options = new CreateCollectionOptions()
                .capped(true)
                .sizeInBytes(10 * MB);

        kytestdb.createCollection(COLLECTION_NAME, options);
        final MongoCollection<Document> collection = kytestdb.getCollection(COLLECTION_NAME);
    }

    @Test
    public void shouldListCollection() {
        kytestdb.createCollection(COLLECTION_NAME);
        kytestdb.createCollection(COLLECTION_NAME2);
        final List<String> actualCollectionNames = kytestdb.listCollectionNames().into(new ArrayList<>());

        assertThat("returned list should contain " + COLLECTION_NAME + " and " + COLLECTION_NAME2,
                actualCollectionNames, hasItems(COLLECTION_NAME, COLLECTION_NAME2));


    }
}