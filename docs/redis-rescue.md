## Replacing Zustand backend with Redis

_Zustand_ has proven to be a formidable tool for state management in the backend, but this is not its area of specialty. What if the _backend zustand_ store was replaced by a different technology with different scaling and performance characteristics? Something like say, Redis?

_[Redis](https://redis.io/)_ has both the characteristics of fast, in-memory storage and pub/sub capability which made _zustand_ quite a formidable option. The big difference however, is that this is the stomping grounds for _Redis_ and its contemporary sidekicks. _Redis_ has a 
lot more enterprise-ready, backend features which _zustand_ cannot begin to dream of like

- ACID transactions
- Database replication and sharding
- Streaming capability
- Horizontal scaling
- etc

Anyway, I will try to recreate something similar to what _zustand_ accomplished, and then try to get things to go further along using the _Redis_ way.

26. Spin up a local _Redis_ server using docker. This is the easiest way to get _Redis_ running without having to install it in your machine, or regardless of what kind of machine you have

Start _Redis_ instance and exec into it in two steps

```bash
docker run -p 6379:6379 -d --name local-redis --restart always redis/redis-stack-server:latest

docker exec -it local-redis redis-cli
```

Or alternatively, you can accomplish the same in just one step

```bash
docker run -p 6379:6379 -it redis/redis-stack-server:latest redis-cli
```

27. Install a suitable _Redis_ driver for nodejs

```bash
npm i redis
```

> Detour - A quick Redis cheatsheet

- Simple Values
    - __SET__ key value [EX seconds|PX millis]
    - __GET__ key
    - __DEL__ key [key...]
    - __TTL__
- Redis List
    - __rpush__ key value [value...]
    - __lpush__ key value [value...]
    - __rpop__ key
    - __lpop__ key
    - __lrange__ key start stop
    - __ltrim__ key start stop
    - __lrem__ key count value
    - __lset__ key idx element
- Redis Hash
    - __hset__ key field value
    - __hget__ key field
    - __hgetAll__ key
- Redis Transactions
    - multi
    - exec

28. Create a _Redis_ file with connector functions to handle requests between the backend and the _Redis_ server.

```bash
touch store/reddy.js

const {createClient} = require('redis');

exports.getAll = async function (listName) {
    try {
        const client = await createClient()
            .on('error', err => console.log('Redis Client Error', err))
            .connect();

        const todos = await client.lRange(listName, 0, -1);
        return todos.map(JSON.parse)
    } catch (e) {
        console.log(e)
    }
}

exports.add = async function (listName, title) {
    try {
        const client = await createClient()
            .on('error', err => console.log('Redis Client Error', err))
            .connect();

        const todo = ({title, done: false})

        await client.rPush(listName, JSON.stringify(todo));
        await client.disconnect();
    } catch (e) {
        console.log(e)
    }
}

exports.toggle = async function (listName, title) {
    try {
        const client = await createClient()
            .on('error', err => console.log('Redis Client Error', err))
            .connect();

        const todos = await client.lRange(listName, 0, -1);
        for (let i = 0; i < todos.length; i++) {
            let todo = JSON.parse(todos[i]);
            if (todo.title === title) {
                todo.done = !todo.done
                await client.lSet(listName, i, JSON.stringify(todo));
            }
        }
        await client.disconnect();
    } catch (e) {
        console.log(e)
    }
}
```

29. With the connectors completed, it's time to wire them up to the _express_ handlers

```bash
touch routes/reddy-todos.js

const express = require('express');
const router = express.Router();
const {getAll, add, toggle} = require('../store/reddy');

router.get('/', async function(req, res, next) {
  res.json(await getAll("todos"));
});

router.post('/:title', async function(req, res, next) {
  const title = req.params['title'];
  await add("todos", title);
  res.sendStatus(200);
});

router.put('/:title', async function(req, res, next) {
  const title = req.params['title'];
  await toggle("todos", title);
  res.sendStatus(200);
});
```

In the _routes/index.js__ file, update the __todoRoutes__ to make use of the _Redis_ database

```js
const todoRoutes = require('./reddy-todos');
```

At this point, the application will successfully save and retrieve data from _Redis_, but the new values will not be reflected in the UI without a manual refresh. This is because _SSE_ is not yet connected to _Redis pub/sub_.
It would just be easy to simply return the _Redis_ results in the express handlers, but this would be falling back to _synchronous request/response_ pattern. The idea here is to stay of the _asynchronous_ track.

30. Hook into _Redis pub/sub_

This will require creating a module that is mostly similar to _events.js_, except it's using _Redis pub/sub_.

```bash
touch routes/todo-events.js

const express = require('express');
const router = express.Router();
const {createClient} = require('redis');

const writers = {}

router.get('/:user', async function (req, res) {
    ...<similar to routes.js>

    //Now handle business
    let counter = 0;

    const listener = ((message, channel) => {
        console.log('channel\n\n', channel, 'message\n\n', message);
        if (channel === "add") {
            res.write('event: add\n');
            res.write(`data: ${message}\n`);
            res.write(`id: ${counter}\n\n`);
        } else {
            res.write('event: toggle\n');
            res.write(`data: ${message}\n`);
            res.write(`id: ${counter}\n\n`);
        }
        counter++;
    })

    try {
        const client = await createClient()
            .on('error', err => console.log('Redis Client Error', err))
            .connect();

        await client.subscribe(['add', 'toggle'], listener);
    } catch (e) {
        console.log(e)
    }

    // Close the connection when the client disconnects
    req.on('close', () => res.end('OK'))
});

module.exports = router;
```

But for this to work, the _publishing_ must be triggered somewhere. This will be accomplished by adding a _publish_ statement in the handler functions

```js
exports.add = async function (listName, title) {
  try {
    //<same as before>
    await client.rPush(listName, todoStr);
    await client.publish("add", todoStr);   // add statement to publish event
    await client.disconnect();
  } catch (e) {
    console.log(e)
  }
}

exports.toggle = async function (listName, title) {
  try {
    //<same as before>
    await client.publish("toggle", JSON.stringify(payload));      // add statement to publish event
    await client.disconnect();
  } catch (e) {
    console.log(e)
  }
}
```

And that should wrap up the experiment with Riot, Node.js and Redis. The choice to use a different UI framework was quite intentional—the idea was to break away from normal, and solve the problem using a different paradigm and framework.
It's easy to try to solve all problems the same way when all the tools looks the same.

## Conclusions

- It takes some getting used to, but async request/response is a great option for backend where horizontal scaling is a huge criteria
- _Redis_ already solves a lot of challenges with backend scaling, but it's not the only solution. There are certain limitations that come with _Redis_ which you much understand before using it
    - it follows a fire-and-forget paradigm. Think of it as a radio station. The radio station will not re-transmit their signal if you didn't catch a word. You have to live with the implications of that design choice.
    - it is synchronous; in that, just like a radio station where you must be tuned in to catch the transmission, the consumers of _Redis_ similarly have to be available to receive messages
    - it follows a fan-out approach. Again, like a radio station, where the transmission is not targeted to one user only or a handful of users, the messages from _Redis_ are similarly not designated for a particular listener.
      Anyone listening on a channel will get all the messages sent to that channel
- Tools like _Kafka_ (java) and _Nats_ (Go), and more so with _kafka_, have very elaborate strategies that can be configured to achieve other features like. And the price of this power and flexibility is of course the increased 
complexity of properly using these tools
  - point-to-point messaging
  - topic partitions
  - consumer groups
  - append-only logs (file persistence)
  - message replay capability
  - delivery acknowledgement strategies
  - etc
- _Zustand_ is a really solid framework for state management, and it really shines through in the UI, where it works even in standalone mode.
- Riot js is a true breath of fresh air. The noise from oiling the gears of React js just completely vanishes, and development is once again blissful. This is not a knock against React. I'm just stating an observation.
