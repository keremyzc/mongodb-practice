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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created by keremyzc on 08/12/2015.
 */
public class MongoQueryTest {


    public static final String DATABASE_NAME = "kytestdb";
    public static final String COLLECTION_NAME = "users";
    public static final int SINGLE = 1;
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
    public void deleteTestData() {
        usersCollection.deleteOne(new Document("name", "john"));
        usersCollection.deleteOne(new Document("name", "mario"));
    }


    @AfterClass
    public static void tearDownClass() throws Exception {
        mongoClient.close();
        injector = null;
    }


    @Test
    public void shouldFindAllDocuments() throws Exception {
        final FindIterable<Document> cursor = usersCollection.find();
        final List<Document> docs = FluentIterable.from(cursor).toList();

        assertThat("test1 and test2 data couldn't be found, mystery?", docs, hasItems(testUser1, testUser2));
    }

    @Test
    public void shouldGetTheCount() throws Exception {
        final long expectedCount = 2L;
        final long actualCount = usersCollection.count();

        assertThat("there should have been two test data", actualCount, is(equalTo(expectedCount)));
    }

    /**
     * When find is executed and a DBCursor is executed you have a pointer to a database document.
     * This means that the documents are fetched in the memory as you call next() method on the DBCursor.
     * <p>
     * but we can eagerly load all the data into the memory
     *
     * @throws Exception
     */
    @Test
    public void shouldEagerlyFetchData() throws Exception {
        final List<Document> users = usersCollection.find().into(new ArrayList<>());

        assertThat(users, hasItems(testUser1, testUser2));
    }

    @Test
    public void shouldEagerlyFetchDataAndSkipFirst() throws Exception {
        final List<Document> users = usersCollection.find().skip(SINGLE).into(new ArrayList<>());

        assertThat("first user should have been skipped", users, is(not(hasItems(testUser1))));
        assertThat("test user 2 couldn't be found in the db", users, hasItems(testUser2));
    }

    @Test
    public void shouldEagerlyFetchDataAndLimitResult() throws Exception {
        final List<Document> users = usersCollection.find().limit(SINGLE).into(new ArrayList<>());

        assertThat("first user should have been included", users, hasItems(testUser1));
        assertThat("test user 2 should have been excluded", users, is(not(hasItems(testUser2))));
    }
}