package com.yazici.mongodb.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.yazici.mongodb.practice.bind.ConfigModule;
import com.yazici.mongodb.practice.bind.MongoClientModule;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.*;

import java.util.Arrays;

import static com.mongodb.client.model.Updates.set;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by keremyzc on 08/12/2015.
 */
public class MongoUpdateTest {


    public static final String DATABASE_NAME = "kytestdb";
    public static final String COLLECTION_NAME = "users";
    public static final long SINGLEL = 1L;

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


    /**
     * from book: Please note that the DBCollection class overloads the method update
     * with update (DBObject q, DBObject o, boolean upsert, boolean multi).
     * The  rst parameter (upsert) determines whether the database should create the element if it does not exist.
     * The second one (multi) causes the update to be applied to all matching objects.
     *
     * @throws Exception
     */
    @Test
    public void shouldUpdateDocument() throws Exception {
        final Document searchQuery = new Document("name", "john");
        final int newAge = 34;
        final Bson updateQuery = set("age", newAge);

        final UpdateResult updateResult = usersCollection.updateOne(searchQuery, updateQuery);

        assertThat("only a single document should have matched", updateResult.getMatchedCount(), is(equalTo(SINGLEL)));
        assertThat("only a single document should have been updated", updateResult.getModifiedCount(), is(equalTo(SINGLEL)));
        assertThat("the update query shouldn't have created a record", updateResult.getUpsertedId(), is(equalTo(null)));

        final Document updatedUser = usersCollection.find(searchQuery).first();
        assertThat("update query should have updated the age to: " + newAge, updatedUser.get("age"), is(equalTo(newAge)));

    }

}