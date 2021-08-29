# AWS SDK v2 DynamoDB including Enhanced Client

A repo demonstrating common patterns and use cases for DynamoDB using the AWS SDK v2 for java, including the Enhanced Client.

The various Java SDKs for DynamoDB are enumerated here: https://www.davidagood.com/dynamodb-java-basics/


## Prerequisites

- Have Java installed; This has been tested with Java 11 but may work with other versions

### Connecting to DynamoDB

#### DynamoDB Local or LocalStack

If you have Docker installed you can use 
[DynamoDB Local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html) 
or [LocalStack](https://localstack.cloud/) to start a locally running DynamoDB.

To override the DynamoDB client to connect to the local instance, set the enviroment variable 
`DYNAMODB_LOCAL_URL`.

For example, here's the one-liner to run DynamoDB Local:

```shell
docker run -p 8000:8000 amazon/dynamodb-local
```

Once this is running you can run the example code with the environment variable 
`DYNAMODB_LOCAL_URL=http://localhost:8000`.

#### Connect to Live AWS DynamoDB

- Have an AWS account
- Have the AWS CLI installed and configured

If the environment variable `DYNAMODB_LOCAL_URL` is not set, the default client expects 
to get the credentials from the default credential provider chain, 
same with the region.

## Running The Code

You can run one of these: 

- `src/main/java/com/davidagood/awssdkv2/dynamodb/App.java`
- `src/main/java/com/davidagood/awssdkv2/dynamodb/AppBasic.java`

## Blog Posts Referencing This Repo

- [Working with Heterogeneous Item Collections in the DynamoDB Enhanced Client for Java](https://davidagood.com/dynamodb-enhanced-client-java-heterogeneous-item-collections/)
- [DynamoDB Enhanced Client for Java: Missing Setters Cause Misleading Error or Unexpected Behavior](https://davidagood.com/dynamodb-enhanced-client-java-missing-setters/)
- [DynamoDB Repository Layer Isolation in Java](https://davidagood.com/dynamodb-repository-layer-isolation-in-java/)

## Features

### Repository Layer Isolation

See here: [DynamoDB Repository Layer Isolation in Java](https://davidagood.com/dynamodb-repository-layer-isolation-in-java/)

### Integration Testing with DynamoDB Local

See all the tests ending in "IT", for example:
`src/test/java/com/davidagood/awssdkv2/dynamodb/repository/DynamoDbIT.java`

Some ideas are taken from the AWS SDK:
[here](https://github.com/aws/aws-sdk-java-v2/blob/93269d4c0416d0f72e086774265847d6af0d54ec/services-custom/dynamodb-enhanced/src/test/java/software/amazon/awssdk/extensions/dynamodb/mappingclient/functionaltests/LocalDynamoDb.java).

### Enhanced Client using Static Schema

Build table schema manually, as opposed to using annotations. 
See [here](https://github.com/aws/aws-sdk-java-v2/tree/master/services-custom/dynamodb-enhanced#initialization)

### Create Table If Not Exists

If the table does not exist, it is created at application startup time.

See `com.davidagood.awssdkv2.dynamodb.repository.DynamoDbRepository.createTableIfNotExists`

### Connect to Local DynamoDB

See `com.davidagood.awssdkv2.dynamodb.App.buildDynamoDbClient`

### Immutable Value Classes Using `@DynamoDbImmutable`

See `com.davidagood.awssdkv2.dynamodb.repository.ImmutableBeanItem`.

## TODO

- Versioned attribute, https://github.com/aws/aws-sdk-java-v2/tree/master/services-custom/dynamodb-enhanced#versionedrecordextension
- Integration testing with DynamoDBLocal
  - Test/demo features such as:
    - DynamoDbTable scan without filtering on type should fail when entity is marshalled, for example see Delivery#setType
    - PutItem fails if item already exists, see com.davidagood.awssdkv2.dynamodb.App.insertCustomerDoNotOverwrite
- Running DynamoDB locally via LocalStack
- Versioning strategy shown here: https://youtu.be/HaEPXoXVf2k?t=2294
- Pagination example

### Entities

- Regular
- Immutable with Lombok
- Immutable without Lombok
- Encapsulated with MapStruct (DynamoDb Immutables not even necessary then?)

## Point AWS CLI to DynamoDB Local

It may be occasionally useful to use the AWS CLI for troubleshooting while using DynamoDB Local. 
Here's an example of how to do that: 

`AWS_ACCESS_KEY_ID=dummy AWS_SECRET_ACCESS_KEY=dummy aws dynamodb list-tables --endpoint-url http://localhost:8000`
