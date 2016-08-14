# Unidirection web application &nbsp;[![Build Status](https://travis-ci.org/ArchDev/unidirectional-akka-redux.svg?branch=master)](https://travis-ci.org/ArchDev/unidirectional-akka-redux)  
In this repository you can find example of unidirection web application builded with [React](https://facebook.github.io/react/), [Redux](http://redux.js.org) and [Akka](http://akka.io). Project done on [Scala](http://www.scala-lang.org) and [ES6](https://nodejs.org/en/docs/es6). Communication between client and server working on top of websocket connection with JSON API. To do some changes on server, client must put command in channel, server will handle it and produce event that will be published across connected clients.

###[Working example!](https://archdev.github.io/unidirectional-akka-redux/)

## Frontend
On a client side, as mentioned before we used React/Redux and ES6. High level logic described in [app.es6](https://github.com/ArchDev/unidirectional-akka-redux/blob/master/frontend/src/app.es6), we have two stores, one for commands and one for events. Commands store used to put event on websocket connection. Events store used by websocket connection to publish changes on all components.

### Components
In project defined [two react components](https://github.com/ArchDev/unidirectional-akka-redux/blob/master/frontend/src/components.es6). Share component have a input form that put PublishTweet command to store. Tweet component its a view of last tweet in system that updates from events store.

### Build tool
As a build tool used [Gulp](http://gulpjs.com) with predefined scripts:

- `gulp compile` - transpile, uglify and bundle all ES6 code to `/dist/app.js`
- `gulp test` - launch test suites with Mocha
- `gulp build` - run test and compile phases
- `gulp serve` - launch webserver with `/dist` folder as a root
- `gulp` - launch serve phase and do watching on tests and sources

## Backend
Server side in current example had a role of state storage that publish changes to all subscribed nodes. Main logic of application you can find in [Tweet Room Actor](https://github.com/ArchDev/unidirectional-akka-redux/blob/master/backend/src/main/scala/me/archdev/TweetRoomActor.scala).

### Request handling
As mentioned before, we used websocket connection to communicate between client and server.
Websocket [connection handled](https://github.com/ArchDev/unidirectional-akka-redux/blob/master/backend/src/main/scala/me/archdev/websocket/WebSocketMessageHandler.scala) by Akka Streams, on it we deserialize our requests, put it into [dispatcher](https://github.com/ArchDev/unidirectional-akka-redux/blob/master/backend/src/main/scala/me/archdev/RouteActor.scala) to choose actor that will handle command and then serialize request.


### Protocol
We have [external](https://github.com/ArchDev/unidirectional-akka-redux/blob/master/backend/src/main/scala/me/archdev/api/external/Protocol.scala) and [internal](https://github.com/ArchDev/unidirectional-akka-redux/blob/master/backend/src/main/scala/me/archdev/api/internal/Protocol.scala) parts of protocol. This separation needed to control flow communication flow and be sure that user cannot execute some internal commands. To be clear, internal protocol part in project needed only for pattern matching in some places so you can easily remove it, because internal protocol always described near actor that will handle it.

## Copyright
Copyright (C) 2016 Arthur Kushka.   
Distributed under the MIT License.


