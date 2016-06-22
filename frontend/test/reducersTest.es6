import expect from 'expect'
import { createStore } from 'redux'

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
