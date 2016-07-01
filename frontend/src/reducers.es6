export const eventReducer = (state = {tweet: ""}, event) => {
  switch (event.type) {
    case 'TweetPublished':
      return Object.assign({}, state, {
        tweet: event.tweet
      })
    default:
      return state
    }
}

export const commandReducer = (state = {lastCommand: {}}, command) => {
  switch (command.type) {
    case "PublishTweet":
      return {lastCommand: command};
    default:
      return state;
    }
}
