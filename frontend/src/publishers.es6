export const commandPublisher = (commandStore, websocket) => {
  return commandStore.subscribe(() => {
    websocket.send(JSON.stringify(commandStore.getState().lastCommand))
  })
}
