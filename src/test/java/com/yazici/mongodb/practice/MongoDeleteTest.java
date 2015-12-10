package com.yazici.mongodb.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.yazici.mongodb.practice.bind.ConfigModule;
import com.yazici.mongodb.practice.bind.MongoClientModule;
import org.bson.Document;
import org.junit.*;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by keremyzc on 08/12/2015.
 */
public class MongoDeleteTest {


    public static final String DATABASE_NAME = "kytestdb";
    public static final String COLLECTION_NAME = "users";
    public static final int SINGLE = 1;
    public static final long SINGLEL = 1L;
    public static final Document QUERY_FOR_USER1 = new Document("name", "john");

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
    public void shouldFindAndDeleteDocument() throws Exception {
        final Document searchQuery = new Document("name", "john");

        final Document actualUser1 = usersCollection.findOneAndDelete(searchQuery);
        assertThat("testUser1 should have been deleted and returned", actualUser1, is(equalTo(testUser1)));

        final Document deletedUser = usersCollection.find(searchQuery).first();
        assertThat("test user 1 shouldnt have been found", deletedUser, is(nullValue()));
    }

    @Test
    public void shouldDeleteDocument() throws Exception {
        final Document searchQuery = new Document("name", "john");

        final DeleteResult result = usersCollection.deleteOne(searchQuery);
        assertThat("testUser1 should have been deleted and delete result should have count one", result.getDeletedCount(), is(equalTo(SINGLEL)));

        final Document deletedUser = usersCollection.find(searchQuery).first();
        assertThat("test user 1 shouldnt have been found", deletedUser, is(nullValue()));
    }

}