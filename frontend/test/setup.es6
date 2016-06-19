import JsDom from "jsdom"

global.document = JsDom.jsdom('<!doctype html><html><body></body></html>')
global.window = document.defaultView
global.navigator = {userAgent: 'node.js'}
