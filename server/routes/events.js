const express = require('express');
const router = express.Router();
const store = require('../store');

const writers = {}

router.get('/:user', function (req, res, next) {
  const user = req.params["user"];
  if (writers[user]) {
    console.log(`closing previous connection before saving a new one for ${user}`)
    writers[user].end('OK')
  }
  writers[user] = res;

  //keep connection open
  res.set({
    'Cache-Control': 'no-cache',
    'Content-Type': 'text/event-stream',
    'Connection': 'keep-alive'
  });
  res.flushHeaders();

  // Tell the client to retry every 10 seconds if connectivity is lost
  res.write('retry: 10000\n\n');

  //Now handle business 
  let counter = 0;

  store.subscribe((state, prev) => {
    console.log('state\n\n', state, 'prev\n\n', prev);
    if(state.todos.length > prev.todos.length){
      res.write('event: add\n');
      res.write(`data: ${JSON.stringify(state.todos.slice(-1)[0])}\n`);
      res.write(`id: ${counter}\n\n`);
      counter++;
      return;
    }
    
    res.write('event: add\n');
    res.write(`data: ${JSON.stringify(state.todos)}\n`);
    res.write(`id: ${counter}\n\n`);
    counter++;
  })

   // Close the connection when the client disconnects
   req.on('close', () => res.end('OK'))
});

module.exports = router;
