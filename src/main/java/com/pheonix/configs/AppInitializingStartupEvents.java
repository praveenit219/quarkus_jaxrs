package com.pheonix.configs;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class AppInitializingStartupEvents {
	
	private static final Logger log = LoggerFactory.getLogger(AppInitializingStartupEvents.class);

   
    @Inject
    SecretKeyGenerator secretKeyGenerators;
    
    @Inject
    KeyPairGenerator keyPairGenerators;

    @Inject
    JsonWebKeyPairInitializer jsonWebKeyPairInitializer;
    
    @Inject
    JsonWebSecretKeyInitializer jsonWebSecretKeyInitializer;
    
    @Inject
    KeyHashGenerator keyHashGenerator;
    
    
    void onStart(@Observes StartupEvent ev) {
        log.info("The application is starting...with..");
        log.info("generating json secret keys....");
        secretKeyGenerators.loadCheckSum();
        log.info("generating json key pairs....");
        keyPairGenerators.loadKeys();
        log.info("generating json web key pair with  key pairs....");
        jsonWebKeyPairInitializer.loadJsonWebKeys();
        log.info("generating json web key pair with  secret keys....");
        jsonWebSecretKeyInitializer.loadJsonWebKeys();
        log.info("generating hash values for keys....");
        keyHashGenerator.hashGenerator();
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping... {}");
    }

}
