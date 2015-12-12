package com.yazici.mongodb.practice.domain.codec;

import com.yazici.mongodb.practice.domain.UserDoc;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Created by keremyzc on 12/12/2015.
 */
public class UserCodec implements Codec<UserDoc> {

    private final Codec<Document> documentCodec;

    public UserCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }


    @Override
    public UserDoc decode(BsonReader reader, DecoderContext decoderContext) {
        return new UserDoc(documentCodec.decode(reader, decoderContext));
    }

    @Override
    public void encode(BsonWriter writer, UserDoc value, EncoderContext encoderContext) {
        documentCodec.encode(writer, value, encoderContext);
    }

    @Override
    public Class<UserDoc> getEncoderClass() {
        return UserDoc.class;
    }
}
