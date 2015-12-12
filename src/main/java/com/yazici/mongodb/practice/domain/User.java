package com.yazici.mongodb.practice.domain;

import com.google.common.base.Objects;
import org.bson.Document;
import org.bson.types.ObjectId;


/**
 * Created by keremyzc on 12/12/2015.
 */
public class User extends Document {


    public static final String NAME = "name";
    public static final String AGE = "age";

    public User(String name, int age) {
        append(NAME, name);
        append(AGE, age);
    }

    public User(Document decode) {
       putAll(decode);
    }

    public ObjectId getObjectId() {
        return getObjectId("_id");
    }

    public String getName() {
        return getString(NAME);
    }

    public int getAge() {
        return getInteger(AGE);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("_id", getObjectId())
                .add("name", getName())
                .add("age", getAge())
                .toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User that = (User) o;
        return getObjectId().equals(that.getObjectId()) &&
                getAge() == that.getAge() &&
                getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getObjectId(), getName(), getAge());
    }
}
