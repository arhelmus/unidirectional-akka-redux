import { ShareComponent, TweetComponent } from './components.es6'
import { eventReducer, commandReducer } from './reducers.es6'

import { createStore } from 'redux'
import React from 'react'
import ReactDOM from 'react-dom'

const eventStore = createStore(eventReducer)
const commandStore = createStore(commandReducer)

ReactDOM.render(
  <content>
    <div className='logo'>
      <img src='./assets/logo.png' title='TWEET THE WORLD' alt='Tweet the world, share your opinion' />
    </div>
    <TweetComponent eventStore={eventStore}/>
    <ShareComponent/>
  </content>, document.getElementById('content_wrapper'))
