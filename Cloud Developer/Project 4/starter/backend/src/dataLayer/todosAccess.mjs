import { DynamoDB } from '@aws-sdk/client-dynamodb'
import { DynamoDBDocument } from '@aws-sdk/lib-dynamodb'
import AWSXRay from 'aws-xray-sdk-core'
import { createLogger } from '../utils/logger.mjs'

const logger = createLogger('todosAccess')

export class TodosAccess {
  constructor(
    documentClient = AWSXRay.captureAWSv3Client(new DynamoDB()),
    todosTable = process.env.TODOS_TABLE,
    todosIndex = process.env.TODOS_CREATED_AT_INDEX
  ) {
    this.documentClient = documentClient
    this.dynamoDbClient = DynamoDBDocument.from(this.documentClient)
    this.todosTable = todosTable
    this.todosIndex = todosIndex
  }

  async getTodos(userId) {
    logger.info(`DATA LAYER: Processing get todo by user id: ${userId}`)

    const res = await this.dynamoDbClient.query({
      TableName: this.todosTable,
      IndexName: this.todosIndex,
      KeyConditionExpression: 'userId = :userId',
      ExpressionAttributeValues: {
        ':userId': userId
      }
    })

    return res.Items
  }

  async createTodo(todoItem) {
    logger.info(`DATA LAYER: Processing create todo with data ${todoItem}`)
    const res = await this.dynamoDbClient.put({
      TableName: this.todosTable,
      Item: todoItem
    })
    logger.info(`DATA LAYER: Created todo with response ${res}`)

    return todoItem
  }

  async updateTodo(todoId, userId, updatedTodo) {
    logger.info(
      `DATA LAYER: Processing update todo with data ${updatedTodo} by user id: ${userId} and todo id: ${todoId}`
    )
    await this.dynamoDbClient.update({
      TableName: this.todosTable,
      Key: {
        todoId,
        userId
      },
      UpdateExpression: 'set #name = :name, dueDate = :dueDate, done = :done',
      ExpressionAttributeValues: {
        ':name': updatedTodo.name,
        ':dueDate': updatedTodo.dueDate,
        ':done': updatedTodo.done
      },
      ExpressionAttributeNames: {
        '#name': 'name'
      },
      ReturnValues: 'UPDATED_NEW'
    })

    return updatedTodo
  }

  async deleteTodo(todoId, userId) {
    logger.info(
      `DATA LAYER: Processing delete todo with user id: ${userId} and todo id: ${todoId}`
    )
    const res = await this.dynamoDbClient.delete({
      TableName: this.todosTable,
      Key: {
        todoId,
        userId
      }
    })

    return res
  }
}
