export const publishTweet = (tweet) => {
  return {
    type: "PublishTweet",
    message: tweet
  }
}
