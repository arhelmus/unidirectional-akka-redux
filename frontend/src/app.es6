import { ShareBlock, TweetBlock } from './components.es6'
import { tweetEventReducer, tweetCommandReducer } from './reducers.es6'

import { createStore } from 'redux'
import React from 'react'
import ReactDOM from 'react-dom'

const tweetEventStore = createStore(tweetEventReducer)
const tweetCommandStore = createStore(tweetCommandStore)

ReactDOM.render(
  <content>
    <div className='logo'>
      <img src='./assets/logo.png' title='TWEET THE WORLD' alt='Tweet the world, share your opinion' />
    </div>
    <TweetBlock/>
    <ShareBlock/>
  </content>, document.getElementById('content_wrapper'))
