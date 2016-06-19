import expect from 'expect'
import TestUtils from 'react-addons-test-utils'
import React from "react"
import ReactDOM from "react-dom"

import { TweetBlock, ShareBlock } from "./../src/components.es6"

describe('Tweet block', () => {

  it('should have default tweet on create', () => {
      const tweetBlock = TestUtils.renderIntoDocument(<TweetBlock/>)
      const domNode = ReactDOM.findDOMNode(tweetBlock)
      expect(domNode.textContent).toEqual("Wait a second, we a loading...")
  })

})

describe('Share block', () => {

  it('should have empty state on create', () => {
    const shareBlock = TestUtils.renderIntoDocument(<ShareBlock/>)
    const domNode = ReactDOM.findDOMNode(shareBlock)
    expect(domNode.querySelectorAll("input")[0].value).toEqual("")
  })

})
