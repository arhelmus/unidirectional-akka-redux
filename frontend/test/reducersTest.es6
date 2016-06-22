import expect from 'expect'
import { createStore } from 'redux'

import * as Commands from './../src/commands.es6'
import { eventReducer, commandReducer } from './../src/reducers.es6'

describe('Event reducer', () => {

  function createContext() {
    const eventStore = createStore(eventReducer)

    return {
      eventStore: eventStore
    }
  }

  it ('should change tweet state on TweetPublished event', () => {
    const { eventStore } = createContext()
    eventStore.dispatch({type: 'TweetPublished', message: 'Test'})
    expect(eventStore.getState().tweet).toBe('Test')
  })

})

describe('Command reducer', () => {

  function createContext() {
    const commandStore = createStore(commandReducer)

    return {
      commandStore: commandStore
    }
  }

  it('should change last command in state', () => {
    const { commandStore } = createContext()
    commandStore.dispatch(Commands.publishTweet("Test"))
    expect(commandStore.getState().lastCommand).toEqual(Commands.publishTweet("Test"))
  })

})
