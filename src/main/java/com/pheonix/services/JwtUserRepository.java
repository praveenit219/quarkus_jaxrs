package com.pheonix.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.pheonix.entity.JwtEntity;
import com.pheonix.pojo.JwtTokenResponse;
import com.pheonix.pojo.JwtUpdateResponse;
import com.pheonix.pojo.TokenRequest;

@Named
@Singleton
public class JwtUserRepository {

	private static final Logger log = LoggerFactory.getLogger(JwtUserRepository.class);
	private static final String REPO_ENTITY = "jwtDetails";
	private static final String DB = "samlJwt";

	
	MongoClient mongoClient;
	MongoCollection<JwtEntity> mongoCollection;
	
	@Inject 
	public JwtUserRepository(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
		mongoCollection = getCollection();
		createIndexes();
	}


	public JwtEntity saveJwtEntity(TokenRequest tokenRequest, JwtTokenResponse jwtResponse) {
		if(log.isDebugEnabled()) 
			log.debug("saving jwtInformation to db for Authorized user {}",tokenRequest.getUser());
		JwtEntity jwtEntity =	new JwtEntity();
		createJwtEntity(jwtEntity, jwtResponse, tokenRequest);
		mongoCollection.insertOne(jwtEntity);
		log.info("saved jwtInformation to db ");	
		return jwtEntity;
	}

	private void createJwtEntity(JwtEntity jwtEntity, JwtTokenResponse jwtResponse, TokenRequest tokenRequest) {
		String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		jwtEntity.setCreateDate(dateNow);
		jwtEntity.setCreatedBy(tokenRequest.getUser());
		jwtEntity.setId(tokenRequest.getId());
		jwtEntity.setComment("created");
		jwtEntity.setJwtToken(jwtResponse.getToken());
		jwtEntity.setModifiedBy(tokenRequest.getUser());
		jwtEntity.setModifiedDate(dateNow);
		jwtEntity.setStatus(1);
		jwtEntity.setUser(tokenRequest.getUser());
		jwtEntity.setExpiration(jwtResponse.getExpiration());
	}


	public int verifyJwtTokenStatus(String jwtToken) {
		if(log.isDebugEnabled()) 
			log.debug("finding  jwtInformation from db for token {}",jwtToken);		
		JwtEntity jwtEntity = mongoCollection.find(Filters.eq("jwtToken", jwtToken)).first();
		log.info("identified jwtInformation from db");	
		return (null!=jwtEntity)?jwtEntity.getStatus():-1;
	}


	public JwtUpdateResponse updateTokenStatus(String modifiedBy, String deletionId) {
		if(log.isDebugEnabled()) 
			log.debug("finding  jwtInformation from db for requestId {}",deletionId);	
		JwtUpdateResponse deleteJwtStatus = null;
		String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
		options.returnDocument(ReturnDocument.AFTER);

		JwtEntity jwtEntity = mongoCollection.findOneAndUpdate(
				Filters.eq("_id", deletionId), 
				Updates.combine(Updates.set("status", 0), Updates.set("modifiedDate", dateNow), Updates.set("modifiedBy", modifiedBy), Updates.set("comment", "invalidated")),
				options);

		if(null!=jwtEntity) {
			deleteJwtStatus = new JwtUpdateResponse();
			deleteJwtStatus.setJwtToken(jwtEntity.getJwtToken());
			deleteJwtStatus.setModifiedBy(jwtEntity.getModifiedBy());
			deleteJwtStatus.setModifiedDate(jwtEntity.getModifiedDate());
			deleteJwtStatus.setStatus((jwtEntity.getStatus()==0)?"inactive":null);
		}
		 
		log.info("updated jwtInformation to db ");	
		return deleteJwtStatus;
	}

	private void createIndexes(){
		mongoCollection.createIndex(Indexes.ascending("_id"));
		mongoCollection.createIndex(Indexes.text("jwtToken"));
	}

	private MongoCollection<JwtEntity> getCollection(){
		return mongoClient.getDatabase(DB).getCollection(REPO_ENTITY, JwtEntity.class);		
	}
}
