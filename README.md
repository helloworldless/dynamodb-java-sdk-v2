# AWS SDK v2 DynamoDB including Enhanced Client

## Blog Post

https://davidagood.com/dynamodb-enhanced-client-java-heterogeneous-item-collections/

## TODO

- Build table schema manually, i.e. without annotations
  - See [here](https://github.com/aws/aws-sdk-java-v2/tree/master/services-custom/dynamodb-enhanced#initialization)
- Create table programmatically
- Versioned attribute
- Conditional `PutItem` if item does not already exist
  - Already exists, just use it/show how to use it: com.davidagood.awssdkv2.dynamodb.App.insertCustomerDoNotOverwrite
- Integration testing with DynamoDBLocal
- Running DynamoDB locally via LocalStack