import expect from 'expect'
import TestUtils from 'react-addons-test-utils'
import React from "react"
import ReactDOM from "react-dom"
import { createStore } from 'redux'

import { TweetComponent, ShareComponent } from "./../src/components.es6"
import { eventReducer, commandReducer } from './../src/reducers.es6'

describe('Tweet component', () => {

  function createContext() {
    const eventStore = createStore(eventReducer)
    const renderTweetBlock = () => {
      return ReactDOM.findDOMNode(TestUtils.renderIntoDocument(<TweetComponent eventStore={eventStore}/>))
    }

    return {
      eventStore: eventStore,
      renderTweetBlock: renderTweetBlock
    }
  }

  function expectTweet(renderTweetBlockFunc, message) {
    expect(renderTweetBlockFunc().textContent).toEqual(message)
  }

  it('should have loading message in state on initilization', () => {
      const { renderTweetBlock } = createContext()
      expectTweet(renderTweetBlock, "Wait a second, we a loading...")
  })

  it('should update tweet on TweetPublished event', () => {
    const { eventStore, renderTweetBlock } = createContext()
    const tweetBlock = renderTweetBlock()
    eventStore.dispatch({type: "TweetPublished", message: "Test"})

    expectTweet(() => tweetBlock, "Test")
  })

})

describe('Share component', () => {

  function createContext() {
    const commandStore = createStore(commandReducer)
    const renderShareBlock = () => {
      return ReactDOM.findDOMNode(TestUtils.renderIntoDocument(<ShareComponent commandStore={commandStore}/>))
    }

    return {
      commandStore: commandStore,
      renderShareBlock: renderShareBlock
    }
  }

  function getShareInput(shareBlock) {
    return shareBlock.querySelectorAll("input")[0]
  }

  function submitInput(input) {
    TestUtils.Simulate.keyPress(input, {charCode : 13})
  }

  it('should clear input after submit', () => {
    const { _, renderShareBlock } = createContext()
    const input = getShareInput(renderShareBlock())

    input.value = "Test"
    submitInput(input)

    expect(input.value).toBe("")
  })

  it("should send PublishTweet command on submit of input", () => {
    const { commandStore, renderShareBlock } = createContext()
    const input = getShareInput(renderShareBlock())

    input.value = "Test"
    submitInput(input)

    expect(commandStore.getState().lastCommand).toEqual({type: "PublishTweet", message: "Test"})
  })

})
