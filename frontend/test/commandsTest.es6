import expect from 'expect'

import * as Commands from './../src/commands.es6'

describe('Commands', () => {

  it("should generate PublishTweet command properly", () => {
    const command = Commands.publishTweet("Test")
    
    expect(command.type).toBe("PublishTweet")
    expect(command.tweet).toBe("Test")
  })

})
