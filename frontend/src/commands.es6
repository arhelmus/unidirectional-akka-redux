export const publishTweet = (msg) => {
  return {
    type: "PublishTweet",
    tweet: msg
  }
}
