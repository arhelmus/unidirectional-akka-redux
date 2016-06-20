export const tweetEventReducer = (tweet = "", event) => {
  switch (event.type) {
    case 'TweetPublished':
      return event.message
    default:
      return tweet
    }
}

export const tweetCommandReducer = (command) => {
  switch (command.type) {
    case "PublishTweet":
      // Send tweet to server
      return;
    default:
      return;
    }
}
