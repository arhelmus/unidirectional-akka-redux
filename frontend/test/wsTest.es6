import expect from 'expect'
import { createStore } from 'redux'

import { eventReducer, commandReducer } from './../src/reducers.es6'
import { wsPublisher, wsListner } from './../src/ws.es6'
import * as Commands from './../src/commands.es6'

describe('WS publisher', () => {

  var sentCommand = ""

  function createContext() {
    const sendFunction = (data) => {
      sentCommand = data
    }

    const commandStore = createStore(commandReducer)
    wsPublisher({send: sendFunction}, commandStore)

    return {
      commandStore: commandStore
    }
  }

  it('should send a command by websocket', () => {
    const { commandStore } = createContext()
    commandStore.dispatch(Commands.publishTweet("Test"))

    expect(sentCommand).toEqual('{"PublishTweet":{"tweet":"Test"}}')
  })

})

describe('WS listner', () => {

  function createContext() {
    const fakeWS = {
      onmessage: null
    }

    const eventStore = createStore(eventReducer)
    wsListner(fakeWS, eventStore)

    return {
      fakeWS: fakeWS,
      eventStore: eventStore
    }
  }

  it('should publish received event into eventStore', () => {
    const { fakeWS, eventStore } = createContext()
    fakeWS.onmessage({data: JSON.stringify({"TweetPublished": {tweet: "Test"}})})

    expect(eventStore.getState().tweet).toEqual("Test")
  })

})
