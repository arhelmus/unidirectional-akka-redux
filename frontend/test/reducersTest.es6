import expect from 'expect'
import { createStore } from 'redux'

import { tweetEventReducer, tweetCommandReducer } from './../src/reducers.es6'

describe('Tweet event reducer', () => {

  function createContext() {
    const tweetEventStore = createStore(tweetEventReducer)

    return {
      tweetEventStore: tweetEventStore
    }
  }

  it ('should change tweet state on TweetPublished event', () => {
    const { tweetEventStore } = createContext()
    tweetEventStore.dispatch({type: 'TweetPublished', message: 'Test'})
    expect(tweetEventStore.getState()).toBe('Test')
  })

})
