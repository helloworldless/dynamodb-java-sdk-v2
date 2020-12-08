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

[App.java](https://github.com/helloworldless/dynamodb-java-sdk-v2/blob/master/src/main/java/com/davidagood/awssdkv2/dynamodb/App.java)


## Blog Posts Referencing This Repo

- [Working with Heterogeneous Item Collections in the DynamoDB Enhanced Client for Java](https://davidagood.com/dynamodb-enhanced-client-java-heterogeneous-item-collections/)

## TODO

- Build table schema manually, i.e. without annotations
  - See [here](https://github.com/aws/aws-sdk-java-v2/tree/master/services-custom/dynamodb-enhanced#initialization)
- Create table programmatically
- Versioned attribute
- Conditional `PutItem` if item does not already exist
  - Already exists, just use it/show how to use it: com.davidagood.awssdkv2.dynamodb.App.insertCustomerDoNotOverwrite
- Integration testing with DynamoDBLocal
- Running DynamoDB locally via LocalStack
