# AWS SDK v2 DynamoDB including Enhanced Client

A repo demonstrating common patterns and use cases for DynamoDB using the AWS SDK v2 for java, including the Enhanced Client.

The various Java SDKs for DynamoDB are enumerated here: https://www.davidagood.com/dynamodb-java-basics/


## Prerequisites

- Have Java installed; This has been tested with Java 11 but previous versions may work
- Have the AWS SDK installed and your profile configured
  - We just construct the default client `DynamoDbClient.builder().build()` which expects to get everthing out of your AWS profile
- Have a DynamoDB table by the name of `java-sdk-v2`, or you can change the `TABLE_NAME` property in `App.java`
  - The schema should have a hash key named `PK` of type `String` and a sort key named `SK` of type `String`

## Where To Begin

`src/main/java/com/davidagood/awssdkv2/dynamodb/App.java`

## Blog Posts Referencing This Repo

- [Working with Heterogeneous Item Collections in the DynamoDB Enhanced Client for Java](https://davidagood.com/dynamodb-enhanced-client-java-heterogeneous-item-collections/)
- [DynamoDB Enhanced Client for Java: Missing Setters Cause Misleading Error or Unexpected Behavior](https://davidagood.com/dynamodb-enhanced-client-java-missing-setters/)
- [DynamoDB Repository Layer Isolation in Java](https://davidagood.com/dynamodb-repository-layer-isolation-in-java/)

## Features

### Repository Layer Isolation

See here: [DynamoDB Repository Layer Isolation in Java](https://davidagood.com/dynamodb-repository-layer-isolation-in-java/)

### Integration Testing with DynamoDB Local

Some ideas are taken from the AWS SDK:
[here](https://github.com/aws/aws-sdk-java-v2/blob/93269d4c0416d0f72e086774265847d6af0d54ec/services-custom/dynamodb-enhanced/src/test/java/software/amazon/awssdk/extensions/dynamodb/mappingclient/functionaltests/LocalDynamoDb.java).


## TODO

- Build table schema manually, i.e. without annotations
  - See [here](https://github.com/aws/aws-sdk-java-v2/tree/master/services-custom/dynamodb-enhanced#initialization)
- Create table programmatically
- Versioned attribute, https://github.com/aws/aws-sdk-java-v2/tree/master/services-custom/dynamodb-enhanced#versionedrecordextension
- Immutable value classes using `@DynamoDbImmutable`
- Integration testing with DynamoDBLocal
  - Test/demo features such as:
    - DynamoDbTable scan without filtering on type should fail when entity is marshalled, for example see Delivery#setType
    - PutItem fails if item already exists, see com.davidagood.awssdkv2.dynamodb.App.insertCustomerDoNotOverwrite
- Running DynamoDB locally via LocalStack
- Versioning strategy shown here: https://youtu.be/HaEPXoXVf2k?t=2294
- Pagination example
- Versioning example

### Entities

- Regular
- Immutable with Lombok
- Immutable without Lombok
- Encapsulated with MapStruct (DynamoDb Immutables not even necessary then?)

## Point AWS CLI to DynamoDB Local

It may be occasionally useful to use the AWS CLI for troubleshooting while using DynamoDB Local. 
Here's an example of how to do that: 

`AWS_ACCESS_KEY_ID=dummy AWS_SECRET_ACCESS_KEY=dummy aws dynamodb list-tables --endpoint-url http://localhost:8000`

## List

aws dynamodb put-item --table-name python-test --item '{"PK": {"S": "List"}, "SK": {"S": "List"}, "Value": {"L": [{"S": "0"}]}}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value[0] = :value" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":value": {"S": "00" }}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value[10] = :value" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":value": {"S": "1" }}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value[-10] = :value" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":value": {"S": "-10" }}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value[5] = 5" --expression-attribute-names '{"#Value": "Value"}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value[10] = :value, #Value[20] = :value2" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":value": {"S": "first" }, ":value2": {"S": "second"}}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value[20] = :value, #Value[10] = :value2" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":value": {"S": "first-2" }, ":value2": {"S": "second-2"}}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value = list_append(#Value, :values)" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":values": {"L": [{"S": "list_appen"}, {"S": "123"}]}}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value = list_append(:values, #Value)" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":values": {"L": [{"S": "-1"}]}}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value[0] = if_not_exists(#Value[0], :value)" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":value": {"S": "overwrite-0" }}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "List"}, "SK": {"S": "List"}}' --update-expression "SET #Value[100] = if_not_exists(#Value[1000], :value)" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":value": {"S": "overwrite-?" }}'


## Set

aws dynamodb put-item --table-name python-test --item '{"PK": {"S": "SET"}, "SK": {"S": "SET"}, "Value": {"SS": ["0"]}}'

aws dynamodb update-item --table-name python-test --key '{"PK": {"S": "SET"}, "SK": {"S": "SET"}}' --update-expression "SET #Value = list_append(#Value, ":values")" --expression-attribute-names '{"#Value": "Value"}' --expression-attribute-values '{":values": {"SS": ["1"]}}'