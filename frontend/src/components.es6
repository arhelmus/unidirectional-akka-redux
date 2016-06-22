import React from "react"
import * as Commands from "./commands.es6"

export class ShareComponent extends React.Component {
  constructor(props) {
    super(props)
  }

  shareMention(e) {
    e.preventDefault()
    if (e.charCode == 13) {
      this.props.commandStore.dispatch(Commands.publishTweet(e.target.value))
      e.target.value = ""
    }
  }

  render() {
    return <div className="share-mention">
      <span className="pointer">></span>
      <input type="text" placeholder="share your mention" value="" onKeyPress={this.shareMention.bind(this)}/>
    </div>
  }
}

export class TweetComponent extends React.Component {
  constructor(props) {
    super(props)
    this.state = {tweet: "Wait a second, we a loading..."}
    this.subscribeOnUpdates(this.props.eventStore)
  }

  subscribeOnUpdates(eventStore) {
    eventStore.subscribe(() => this.changeTweet(eventStore.getState().tweet))
  }

  changeTweet(newTweet) {
    this.setState(Object.assign({}, this.state, {
      tweet: newTweet
    }))
  }

  render() {
    return <div className="actual-tweet">{this.state.tweet}</div>
  }
}
