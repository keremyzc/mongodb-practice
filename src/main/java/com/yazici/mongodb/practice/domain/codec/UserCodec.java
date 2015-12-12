package com.yazici.mongodb.practice.domain.codec;

import com.yazici.mongodb.practice.domain.User;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Created by keremyzc on 12/12/2015.
 */
public class UserCodec implements Codec<User> {

    private final Codec<Document> documentCodec;

    public UserCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }


    @Override
    public User decode(BsonReader reader, DecoderContext decoderContext) {
        return new User(documentCodec.decode(reader, decoderContext));
    }

    @Override
    public void encode(BsonWriter writer, User value, EncoderContext encoderContext) {
        documentCodec.encode(writer, value, encoderContext);
    }

    @Override
    public Class<User> getEncoderClass() {
        return User.class;
    }
}
