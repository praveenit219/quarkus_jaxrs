package com.pheonix.mongocodecs;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import com.pheonix.entity.JwtEntity;

public class JwtEntityCodecProvider implements CodecProvider {
	
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == JwtEntity.class) {
            return (Codec<T>) new JwtEntityCodec();
        }
        return null;
    }

}
