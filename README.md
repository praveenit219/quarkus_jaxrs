# Quarkus Graal Native Image!

This project is to tryout new lightweight java framework with native graaljvm and openjdk 8 or 11 jvm. no testcases were written as this is eager to learn and see the differences. this include mainly
>  jax-rs, quarkus-mongo, quarkus-di, quarkus-resteasy, quarkus-jsonb, jose-4j, docker, 

expose rest endpoints to tryout jwe and jws kind of apis for any json body in the form of string.
this is only to learn and see how this works. nothing more to prove or build any products.

<hr>

 1. Packaging
 2. JDK and GraalVm (native iamge)
 3. docker
 
 we are running open jdk8, lets see how the packaging will be 
*to be frank i faced lot of issues., but finally got it working with lot of tweeks* 

## references:
i referred lot of docs and google to many specific issues, but for a quick start and easy moving main links are 

 - [https://quarkus.io/guides/application-configuration-guide#create-the-configuration](https://quarkus.io/guides/application-configuration-guide#create-the-configuration)
 - [https://quarkus.io/guides/native-and-ssl-guide.html#working-with-containers](https://quarkus.io/guides/native-and-ssl-guide.html#working-with-containers)
 - [https://quarkus.io/guides/building-native-image-guide](https://quarkus.io/guides/building-native-image-guide)

## profiles:
i created profiles %stg and %test to see if the profiles work and changed  the mongodb and logger accordingly.

> %test.quarkus.log.category."com.pheonix".level=DEBUG
%stg.quarkus.log.category."com.pheonix".level=INFO
>%test.quarkus.mongodb.connection-string = mongodb://localhost:27017/samlJwt
%stg.quarkus.mongodb.connection-string = 

then while running in local and in docker it differ.
```
for compile and running in local..
mvn clean compile -Dquarkus.profile=stg quarkus:dev
for docker
we need to set in dockerfile as args so docker now while running which profile to use.
```
## lookout for args in pom.xml

 1. i have to use special args for my business logics especially reflections, ipv4 etc. 
 2. i have jose4j dependencies for jwe and jws with custom keystore which require security to be enabled.
 3.  enableJni and enableAllSecurityServices are from quarkus which helps to run graalvm args to -H:+Jni and --enable-all-security-services in the running of graal native image.
 4.  additional Build args are for reflection and resource json file lookup which i need this for reflection and also custom files in src/main/resources folder in my project structure.
 5. graal vm is specific and needs to be properly pointup what it needs rather than java specific usual way. look for graalvm github pages  [https://github.com/oracle/graal/tree/master/substratevm](https://github.com/oracle/graal/tree/master/substratevm)


```xml
<executions>
	<execution>
		<goals>
			<goal>native-image</goal>
		</goals>
		<configuration>
			<enableHttpUrlHandler>true</enableHttpUrlHandler>
			<enableJni>true</enableJni>
			<enableAllSecurityServices>true</enableAllSecurityServices>
			<additionalBuildArgs>
				<additionalBuildArg>-H:ReflectionConfigurationFiles=${project.basedir}/reflection-config.json</additionalBuildArg>
				<additionalBuildArg>-H:ResourceConfigurationFiles=${project.basedir}/resources-config.json</additionalBuildArg>
			</additionalBuildArgs>
		</configuration>
	</execution>
</executions>
```

## complications:
1) i used my own vm which have by default ipv6 enabled. but my openstack vms dont have by default this caused lot of issues.
make sure you have a ipv4 option enabled and ipv6 disabled in your local vm to package and run the docker in ipv4 forcefully
steps :
2) if for any reason you need to connect mongo in replicaset and the hostnames are not be mapped with correct DNS use the "extra_hosts" in docker compose file or use --add-hosts in docker run. 
my application have a secure mongo replica set connection so have to run this or we will face lot of issues for DNS mappings
3) still not working is ... i raised a stackoverflow for this. https://stackoverflow.com/questions/57900399/quarkus-building-a-native-executable-nosuchfileexception-with-dnative-image-doc
 ```shell
./mvnw package -Pnative -Dnative-image.docker-build=true
```

as my packaging is directly in ubuntu which is linux flavor the below command resulted me native image. so i used this directly to use docker build.
```shell
./mvnw package -Pnative *is working fine*
```
4) i also have issue in docker file for native image building, although i need to add cacerts and libsunec.so files for native image to refer in case of java security packages.
> i copied the libsunec.so and cacerts from the graal vm installed location. 

 i couldn't make it work with the suggestion from [https://quarkus.io/guides/native-and-ssl-guide.html#working-with-containers](https://quarkus.io/guides/native-and-ssl-guide.html#working-with-containers)
i had to manually put these files in docker project and then i have to use Dockerfile.native to use these two entries for the packaging. 

## Docker configs
i had to update the default native file to properly match my requirement. although it can be done in best way i only tried to make it work for now. lookout for args in CMD entries.
```
FROM registry.access.redhat.com/ubi8/ubi-minimal
WORKDIR /work/
COPY target/*-runner /work/application
COPY cacerts /work/
COPY libsunec.so /work/lib/
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application","-Dquarkus.http.host=0.0.0.0", "-Dquarkus.profile=stg", "-Djava.library.path=/work/lib", "-Djavax.net.ssl.trustStore=/work/cacerts","-Djava.net.preferIPv4Stack=true"]
```
i used my docker-compose file to setup the ports and also the extra_hosts: for my mongo to refer the replica set host running in different vms.
```property
%stg.quarkus.mongodb.connection-string=mongodb://user:password@mongo-primary:27017,mongo-secondary:27017,mongo-arbiter:27017/samlJwt?replicaSet=test-jwt&authSource=admin&tls=false&ssl=false
```
i cannot share those here. but hopefully its template may look like 
```yml
version: '3'

services:
  ph-jaxrs-nativeimage:
    image: ph-jaxrs-nativeimage:latest
    ports:
      - 11095:11095
    extra_hosts:
          - "mongo-arbiter:1.1.1.1"
          - "mongo-secondary:2.2.2.2"
          - "mongo-primary:3.3.3.3"
```

## jaxrs json issues
i still have issue in returning errors in custom json model in docker container from native image.
this is working fine in normal java way.

native image is not returning the json model properly. im still looking in to it. but rest all looks good for me 

## package footprint

although for simple dependencies the package size varies. for me it looks like with springboot code vs jaxrs code and packaging the docker size looks like below

```
REPOSITORY                  TAG                                   IMAGE ID            CREATED             SIZE
ph-jaxrs-nojvm              latest                                765ad80a7a94        19 hours ago        135MB
springboot-jwt    			latest   							  3ad7e5f46fea        4 days ago          231MB
```
you can notice the size differences. i'm sure it could have been so better. 

## ipv6 issue
this may be only my issue with our cloud hosted vm. secure reasons this was disabled and when i run the docker in that cloud hosted vm i faced issues like below from undertow

> java.net.SocketException: Address family not supported by protocol

i then check that it is due to ipv6 lookup and then my local vm have no issue becz of ipv6 is enabled. we can check by checking below entry in **/etc/sysctl.conf**
> net.ipv6.conf.all.disable_ipv6 = 0

i cannot enable ipv6 in cloud hosted vm so i tried to disable in my localvm and made my docker and native image to use only ipv4 that way i can replicate same vm in my local. so i did it had to disable all ipv6 with the below entries at /etc/sysctl.conf 
from this doc i followed
[https://pario.no/2011/12/09/disable-ipv6-on-ubuntu-11-10/](https://pario.no/2011/12/09/disable-ipv6-on-ubuntu-11-10/)
> net.ipv6.conf.all.disable_ipv6 = 1
> sudo sysctl -p
> ip -a | grep inet
you will not see any inet details in the list

in the application side from the compilation, package, native-image build, docker build i had to use this java Dargs to make sure the native image is using ipv4 so then my app dont have any issues 
```
-Djava.net.preferIPv4Stack=true
```
check for Docker.native and pom.xml and lookout for graal-native image args while packing with maven in the logs you will notive this arg is added in the step while building

## GraalVM
after setup, when you run 
> maven clean package -Pnative

it actually calls the quarkus way of doing native image building with graal. make sure u have setup graal vm in your linux. i have used CE Graal vm in ubuntu for my local vm.

notice the maven build log which have graal vm args what we discussed are appended by quarkus.
```
[INFO] [io.quarkus.creator.phase.nativeimage.NativeImagePhase] Running Quarkus native-image plugin on OpenJDK 64-Bit GraalVM CE 19.2.0
[INFO] [io.quarkus.creator.phase.nativeimage.NativeImagePhase] /usr/lib/jvm/graalvm-ce-19.2.0/jre/bin/native-image -J-Djava.util.logging.manager=org.jboss.logmanager.LogManager -J-Dio.netty.leakDetection.level=DISABLED -J-Dvertx.disableDnsResolver=true -J-Dio.netty.noUnsafe=true -H:ReflectionConfigurationFiles=/shared_data/quarkus_jaxrs/reflection-config.json -H:ResourceConfigurationFiles=/shared_data/quarkus_jaxrs/resources-config.json -Djava.net.preferIPv4Stack=true --initialize-at-build-time= -H:InitialCollectionPolicy=com.oracle.svm.core.genscavenge.CollectionPolicy$BySpaceAndTime -jar ph-quarkus-jwt-1.0-runner.jar -J-Djava.util.concurrent.ForkJoinPool.common.parallelism=1 -H:FallbackThreshold=0 -H:+ReportExceptionStackTraces -H:+PrintAnalysisCallTree -H:-AddAllCharsets -H:EnableURLProtocols=http,https --enable-all-security-services -H:NativeLinkerOption=-no-pie -H:-SpawnIsolates -H:+JNI --no-server -H:-UseServiceLoaderFeature -H:+StackTrace

```

## performance
1) although native image first request took more time, rather the subsequent hits are fast. 
still have to compare spring vs native -image