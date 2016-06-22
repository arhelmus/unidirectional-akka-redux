export const eventReducer = (state = {tweet: ""}, event) => {
  switch (event.type) {
    case 'TweetPublished':
      return Object.assign({}, state, {
        tweet: event.message
      })
    default:
      return state
    }
}

export const commandReducer = (state = {commandHistory: []}, command) => {
  switch (command.type) {
    case "PublishTweet":
      // Send tweet to server
      return {commandHistory: [command, ...state.commandHistory]};
    default:
      return state;
    }
}
