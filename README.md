# Petstore example

Generate petstore controllers using OpenApi generator.

* kotlin
* openapi generator
* reactive spring
* embedded mongo db

## Implementation

PetStore is implemented using `petstore.yaml` openapi as example. This api was 
changed to fit mongo db requirements. Only Pet controller is implemented.

PetStore api was generated using openapi generator gradle plugin.
Controller stubs were produced. Mapper is done manually. 

Model is stored to `ReactiveMongoRepository`. Exception handler
is implemented as controller advice. PetStoreException class was
done to retrieve messages from message.properties file.

## Launch
1. Launch application using `./gradlew bootRun`.
2. Run http requests from pet.http file
