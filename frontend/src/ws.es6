export const wsPublisher = (websocket, commandStore) => {
  return commandStore.subscribe(() => {
    const lastCommand = Object.assign({}, commandStore.getState().lastCommand, {})
    const lastCommandName = lastCommand.type
    delete lastCommand.type

    websocket.send('{"' + lastCommandName + '":' + JSON.stringify(lastCommand) + '}')
  })
}

export const wsListner = (websocket, eventStore) => {
  return websocket.onmessage = (e) => {
    const eventJson = JSON.parse(e.data)
    const eventType = getEventType(eventJson)

    eventStore.dispatch(Object.assign({}, eventJson[eventType], {type: eventType}))
  }
}

function getEventType(eventJson) {
  for(var k in eventJson) return k
}
