import React from "react"

export class ShareBlock extends React.Component {
  constructor(props) {
    super(props)
    this.state = {mention: ""}
  }

  shareMention() {

  }

  render() {
    return <div className="share-mention">
      <span className="pointer">></span>
      <input type="text" placeholder="share your mention" value={this.state.mention}/>
    </div>
  }
}

export class TweetBlock extends React.Component {
  constructor(props) {
    super(props)
    this.state = {tweet: "Wait a second, we a loading..."}
  }

  subscribeOnUpdates() {

  }

  handleTweetChange() {

  }

  render() {
    return <div className="actual-tweet">{this.state.tweet}</div>
  }
}
