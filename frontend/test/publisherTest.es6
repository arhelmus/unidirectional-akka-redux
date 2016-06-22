import expect from 'expect'
import { createStore } from 'redux'

import { _, commandReducer } from './../src/reducers.es6'
import { commandPublisher } from './../src/publishers.es6'
import * as Commands from './../src/commands.es6'

describe('Command store listner', () => {

  var sentCommand = ""

  function createContext() {
    const sendFunction = (data) => {
      sentCommand = data
    }

    const commandStore = createStore(commandReducer)
    const publisher = commandPublisher(commandStore, {send: sendFunction})

    return {
      commandStore: commandStore,
      publisher: publisher
    }
  }

  it('should send a command by websocket', () => {
    const { commandStore } = createContext()
    commandStore.dispatch(Commands.publishTweet("Test"))

    expect(sentCommand).toEqual(JSON.stringify(Commands.publishTweet("Test")))
  })

})
