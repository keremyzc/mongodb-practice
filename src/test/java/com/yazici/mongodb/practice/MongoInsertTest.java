package com.yazici.mongodb.practice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yazici.mongodb.practice.bind.ConfigModule;
import com.yazici.mongodb.practice.bind.MongoClientModule;
import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.or;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;
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

    @After
    public void cleanUp() {
        usersCollection.deleteMany(new Document("name", "romano"));
        usersCollection.deleteMany(new Document("name", "john"));
        usersCollection.deleteMany(new Document("name", "alberto"));
    }


    @Test
    public void shouldInsertASingleDocument() throws Exception {

        final Document user = new Document("name", "john")
                .append("age", 33)
                .append("phone", "0983042");

        usersCollection.insertOne(user);

        final Document actualUser = usersCollection.find(user).first();

        assertThat("it should have inserted a single user: " + user, actualUser, is(equalTo(user)));
    }

    @Test
    public void shouldInsertAnEmbeddedDocument() throws Exception {
        final Document user = new Document("name", "alberto")
                .append("age", 52)
                .append("info",
                        new Document("email", "alberto@gmail.com")
                                .append("phone", "9283742"));

        usersCollection.insertOne(user);
    }

    @Test
    public void shouldInsertAnArrayOfData() throws Exception {
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
        final Document actualUser = usersCollection.find(user).first();

        assertThat("it should have inserted a single user: " + user, actualUser, is(equalTo(user)));
    }

    @Test
    public void shouldInsertMultipleDocument() throws Exception {
        final List<Document> users = new ArrayList<>(3);

        final Document alberto = new Document("name", "alberto")
                .append("age", 52);
        final Document john = new Document("name", "john")
                .append("age", 16);
        final Document tim = new Document("name", "tim")
                .append("age", 22);

        users.add(alberto);
        users.add(john);
        users.add(tim);

        usersCollection.insertMany(users);

        final List<Document> actualUsers = usersCollection.find(or(alberto, john, tim)).into(new ArrayList<>());

        assertThat("it should have inserted multiple users", actualUsers, hasItems(alberto, john, tim));
    }

    /**
     * from the book :: Be careful when using your own IDs!
     * When providing your own keys, it is entirely your responsibility to care for duplicate key issues. Generally speaking, MongoDB documents
     * can share the same keys; that's not true for the _id key, resulting in an exception if you try to insert two documents with the same _id:
     *
     * @throws com.mongodb.MongoException$DuplicateKey
     */
    @Test
    public void shouldInsertWithId() throws Exception {
        final UUID uuid = UUID.randomUUID();

        final Document user = new Document("_id", uuid.toString().replace("-", ""))
                .append("name", "romano")
                .append("age", 28);

        usersCollection.insertOne(user);
        final Document actualUser = usersCollection.find(user).first();

        assertThat("it should have inserted a single user: " + user, actualUser, is(equalTo(user)));
    }
}