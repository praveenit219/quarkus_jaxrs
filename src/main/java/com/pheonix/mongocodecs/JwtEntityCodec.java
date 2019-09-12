package com.pheonix.mongocodecs;

import java.util.UUID;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.pheonix.entity.JwtEntity;
import com.pheonix.utils.StringUtils;



public class JwtEntityCodec implements CollectibleCodec<JwtEntity> {
	
	private static final Logger log = LoggerFactory.getLogger(JwtEntityCodec.class);

	private final Codec<Document> documentCodec;

	public JwtEntityCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
	}


	@Override
	public void encode(BsonWriter writer, JwtEntity jwtEntity, EncoderContext encoderContext) {
		Document doc = new Document();
		doc.put("_id", jwtEntity.getId());
		doc.put("user", jwtEntity.getUser());
		doc.put("jwtToken", jwtEntity.getJwtToken());
		doc.put("expiration", jwtEntity.getExpiration());
		doc.put("status", jwtEntity.getStatus());
		doc.put("createDate", jwtEntity.getCreateDate());
		doc.put("modifiedDate", jwtEntity.getModifiedDate());
		doc.put("createdBy", jwtEntity.getCreatedBy());
		doc.put("modifiedBy", jwtEntity.getModifiedBy());
		doc.put("comment", jwtEntity.getComment());		
		documentCodec.encode(writer, doc, encoderContext);

	}

	@Override
	public Class<JwtEntity> getEncoderClass() {
		return JwtEntity.class;
	}

	@Override
	public JwtEntity decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = documentCodec.decode(reader, decoderContext);
		JwtEntity jwtEntity = new JwtEntity();		
		jwtEntity.setId(document.getString("id"));
		jwtEntity.setComment(document.getString("comment"));
		jwtEntity.setModifiedBy(document.getString("modifiedBy"));
		jwtEntity.setCreatedBy(document.getString("createdBy"));
		jwtEntity.setModifiedDate(document.getString("modifiedDate"));
		jwtEntity.setCreateDate(document.getString("createDate"));
		jwtEntity.setStatus(document.getInteger("status"));
		jwtEntity.setExpiration(document.getString("expiration"));
		jwtEntity.setJwtToken(document.getString("jwtToken"));
		jwtEntity.setUser(document.getString("user"));
		return jwtEntity;
	}

	@Override
	public JwtEntity generateIdIfAbsentFromDocument(JwtEntity document) {
		if (!documentHasId(document)) {			
			document.setId(UUID.randomUUID().toString());
		}
		return document;
	}

	@Override
	public boolean documentHasId(JwtEntity document) {
		log.info("inside document to check id {} {}", document.getId(), !StringUtils.isEmpty(document.getId()));
		return !StringUtils.isEmpty(document.getId());
	}

	@Override
	public BsonValue getDocumentId(JwtEntity document) {
		return new BsonString(document.getId());
	}

}
