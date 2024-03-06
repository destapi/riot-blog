## Pushing Zustan boundaries

21. What if the _zustand_ store is pushed to the backend, so that the data in the _frontend_ store is always in sync with the _backend_ store, and be able to survive a full page refresh? Well, firth things first. I'll create a Nodejs backend

```bash
mkdir server && cd server

npm express-generator --no-view

npm i cors
```

This will generate a bare-bone server application with some endpoints. I'll use the _cors_ middleware to accept requests coming from another domain, in this case, the _dev-server_. I also refactored the routes generated to have only one entry point (for convenience)

```js
const cors = require('cors');   //importing cors middleware

const appRoutes = require('./routes');  // importing refactored routes

const app = express();

app.use(cors());        // added cors middleware
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(appRoutes);     // single entrypoint to routes
```

22. Create a _store_ folder and configure a _zustand_ store

```bash
mkdir store

touch store/index.js

const { createStore } = require ('zustand/vanilla');

module.exports = createStore((set) => ({
  todos: [],  
  add: (title) => set((state) => ({...state, todos: [...state.todos, {title, done: false}]})),
  toggle: (title) => set((state) => ({...state, todos: state.todos.map(s => {
      if(s.title === title){
        return ({...s, done: !s.done});
      }
      return s;
    })})),
}))
```

The _backend zustand_ store has many similarities to the _frontend zustand_ store. The two don't have to be identical, but they will inevitably share a lot of similar characteristics.

23. Now, refactor the _server/routes_ folder to have these files. The idea is to create handlers that will interact with the _zustand_ store above. The implementation is extremely basic and not production-quality, but it is that way for illustration and to easily make the necessary points.

```
server/routes/
|-- events.js
|-- index.js
`-- todos.js
```

> index.js - convergence of _/todos_ and _/events_ handlers into a single entrypoint
```js
const express = require('express');
const router = express.Router();

const todoRoutes = require('./todos');
const eventRoutes = require('./events');

router.use('/todos', todoRoutes);
router.use('/events', eventRoutes);

module.exports = router;
```

> todos.js - only three endpoints are in play for this demonstration - _get all todos_, _add todo_ and _toggle todos_
```js
const express = require('express');
const router = express.Router();
const store = require('../store');      // zustand store

router.get('/', function(req, res, next) {
    res.json(store.getState().todos);
});

router.post('/:title', function(req, res, next) {
    const title = req.params['title'];
    store.getState().add(title);
    res.sendStatus(200);
});

router.put('/:title', function(req, res, next) {
    const title = req.params['title'];
    store.getState().toggle(title);
    res.sendStatus(200);
});

module.exports = router;
```

> events.js - EventSource endpoint. This is where _zustand_ meets _EventSource_
```js
const express = require('express');
const router = express.Router();
const store = require('../store');      // zustand store

const writers = {}

router.get('/:user', function (req, res) {
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

    // subscribe to the store and fan-out updates to the connected clients
    store.subscribe((state, prev) => {
        console.log('state\n\n', state, 'prev\n\n', prev);
        if (state.todos.length > prev.todos.length) {
            res.write('event: add\n');
            res.write(`data: ${JSON.stringify(state.todos.slice(-1)[0])}\n`);
            res.write(`id: ${counter}\n\n`);
        } else {
            res.write('event: toggle\n');
            res.write(`data: ${JSON.stringify(state.todos)}\n`);
            res.write(`id: ${counter}\n\n`);
        }
        counter++;
    })

    // Close the connection when the client disconnects
    req.on('close', () => res.end('OK'))
});

module.exports = router;
```

24. In the _frontend_, the _zustand_ store will need some updates to work with the backend. Instead of receiving events from the frontend UI, the store will now receive events from the backend server.

```js
import {createStore} from 'zustand/vanilla'

export const store = createStore((set) => ({
    todos: [],
    // add: (title) => set((state) => ({...})),
    // toggle: (item) => set((state) => ({...})),
    initTodos: (todos) => set((state) => ({...state, todos: [...state.todos, ...todos]})),
    addTodo: (todo) => set((state) => ({...state, todos: [...state.todos, todo]})),
    toggleTodos: (todos) => set((state) => ({...state, todos})),
}))
```

25. This means that a connection needs to be established to the backend to receive these events. This is accomplished using an _EventSource_ endpoint. In _riot/app.riot_, make the following changes

```js
<script>
    import TodoForm from './todo-form.riot';
    import TodoItems from './todo-items.riot';
    
    const remoteUrl = "http://localhost:3000";
    let unsubscribe;
    export default {
      components: {
        TodoForm,
        TodoItems,
      },
      onBeforeMount(props, state) {
        // initial state
        this.state = {
          items: props.getState(),
        }
      },
      onMounted(props, state){
        unsubscribe = this.props.subscribe(state => {
          this.update({
            items: state.todos
          })
        });
        this.initialTodos();
        this.startEvents();
      },
      onUnmounted(){
        unsubscribe();
      },
      add(title) {
        // this.props.getState().add(title)
        fetch(`${remoteUrl}/todos/${title}`, { method: 'POST', })
                // .then(res => res.json())
                .then(data => {
                  console.log('add todo status', data);
                })
      },
      toggle(item) {
        // this.props.getState().toggle(title)
        fetch(`${remoteUrl}/todos/${item.title}`, { method: 'PUT', })
                // .then(res => res.json())
                .then(data => {
                  console.log('toggle todo status', data);
                })
      },
      startEvents(){
        const sub = new EventSource(`${remoteUrl}/events/jimmy`);
    
        // Default events
        sub.addEventListener('open', () => {
            console.log('Connection opened')
        });
    
        sub.addEventListener('error', () => {
            console.error("Subscription err'd")
        });
    
        sub.addEventListener('add', (event) => {
            const data = JSON.parse(event.data);
            this.props.getState().addTodo(data);
        });
    
        sub.addEventListener('toggle', () => {
            const data = JSON.parse(event.data);
            this.props.getState().toggleTodos(data);
        });
      },
      initialTodos(){
        fetch(`${remoteUrl}/todos`)
        .then(res => res.json())
        .then(data => {
          this.props.getState().initTodos(data);
        })
      },
    }
</script>
```

The only changes made to the file above are as follows:

- the _add_ and toggle methods now send the user input to the backend, instead of updating the _frontend_ store __directly__ and __synchronously__. The actual update to the _frontend_ store will be handled __asynchronously__ from __server-sent events__.
- the _initialTodos_ was added to re-initialize the _frontend_ store using the state that exists in the _backend_ in the event that a full-page refresh happens.
- the _startEvents_ was added to initiate a __SSE__ connection to the server, so that the _frontend_ store can receive update events from the backend.

### key takeaways

- The result of _add_ or _toggle_ events in the frontend are no longer synchronous. Previously, the result would have either been the requested data or an error. This time, the store events are not _tied_ to a particular request. The frontend store can now be updated asynchronously by multiple users.
- Asynchronicity allows the same _todolist_ to be updated remotely by multiple users, but the reliability guarantees of synchronous updates no longer exist. As a system designer, this is a very important consideration
