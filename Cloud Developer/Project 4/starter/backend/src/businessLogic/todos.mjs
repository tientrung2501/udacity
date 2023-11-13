import * as uuid from 'uuid'

import { TodosAccess } from '../dataLayer/todosAccess.mjs'
import {
  getUploadUrl,
  generateAttachmentPresignedUrl
} from '../fileStorage/attachmentsFileStorage.mjs'
import { createLogger } from '../utils/logger.mjs'

const logger = createLogger('todosBussinessLogic')
const todosAccess = new TodosAccess()

async function getTodosByUserId(userId) {
  logger.info(`BUSINESS LOGIC: Processing get todo by user id: ${userId}`)
  return todosAccess.getTodos(userId)
}

async function createTodo(newTodo, userId) {
  logger.info(
    `BUSINESS LOGIC: Processing create todo with data ${newTodo} by user id: ${userId}`
  )

  const todoId = uuid.v4()
  const createdAt = new Date().toISOString()
  const attachmentUrl = generateAttachmentPresignedUrl(todoId)

  return await todosAccess.createTodo({
    userId,
    todoId,
    createdAt,
    done: false,
    attachmentUrl: attachmentUrl,
    ...newTodo
  })
}

async function updateTodo(todoId, userId, updatedTodo) {
  logger.info(
    `BUSINESS LOGIC: Processing update todo with data ${updatedTodo} by user id: ${userId} and todo id: ${todoId}`
  )
  return todosAccess.updateTodo(todoId, userId, updatedTodo)
}

async function deleteTodo(todoId, userId) {
  logger.info(
    `BUSINESS LOGIC: Processing delete todo with user id: ${userId} and todo id: ${todoId}`
  )
  return todosAccess.deleteTodo(todoId, userId)
}

async function createAttachmentPresignedUrl(todoId) {
  logger.info(
    `BUSINESS LOGIC: Processing create attachment url with todo id: ${todoId}`
  )
  return await getUploadUrl(todoId)
}

export {
  getTodosByUserId,
  createTodo,
  updateTodo,
  deleteTodo,
  createAttachmentPresignedUrl
}
