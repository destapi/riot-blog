## The React App components

Out of curiority, I wanted to switch the _Riot_ app with a _React_ app, and understand where there might be some friction during the cross-over.

### Create an basic react app

Although there might be more than one way of bootstrapping a bare-bone _React_ app, my favorite lately has been using _vite_, and that's what I'd recommend. Creatng an empty _React_ app should not require a coffee break (_cra_, I'm looking at you).

```bash
npx create vite@latest

# The ensuing prompts should be self-explanatory and do not require further explanation
```

Prepare the generated project for the use by doing the following:
- remove unnecessary files
- Clean up App.jsx file
```js
function App() {

  return (
    <>
      
    </>
  )
}

export default App
```
- create _components_ and _store_ folders 
- create _index.jsx_ inside _store_ folder
- create _TodoForm.jsx_ and _TodoItems.jsx_ files in the _components_ folder 

> Note that for a _React_ application, _vite_ requires using _.jsx_ extention and enable JSX syntax  

```bash
# completed set up
C:\<base project>\REACT
│   .eslintrc.cjs
│   .gitignore
│   index.html
│   package.json
│   vite.config.js
│
├───public
└───src
    │   App.jsx
    │   main.jsx
    │
    ├───assets
    ├───components
    │       TodoForm.jsx
    │       TodoItems.jsx
    │
    └───store
            index.js
```

The _TodoForm_ seems to be a good place to start. Upon examinig the _riot_ version of the same components in _riot/todo-form.riot_, the similarities are unmistakable. Casually glancing at the two components, the differences apppear almost simply cosmetic, but that's far from the reality. However, we will see further along how the two different design philosophies employed by each of the frameworks give them their unique capabilities.

```js
import { useState } from 'react';

export default function TodoForm(props) {

    const [text, setText] = useState('')

    function edit(e) {
        // update only the text state
        setText(e.target.value)
    }

    function add(e) {
        e.preventDefault()

        if (text) {
            props.add(text)
            setText('');
        }
    }

    return (
        <form onSubmit={add}>
            <label>
                <input onChange={edit} value={text} />
            </label>
            <button disabled={!text}>
                Add #{props.count}
            </button>
        </form>
    )
}
```

Naturally, the next component to work on is the _TodoItems_. Once again upon examinig the _riot_ version of the same components in _riot/todo-items.riot_, the similarities are equally unmistakable. The biggest difference is that the loop function in _riot_ in embedded as an element attribute, while _React_ uses the _nap_ function of array. I personally think having the loop definition done as an attribute is much cleaner and friendlier to the eye. 

```js
export default function TodoItems(props){

    return (
        <ul>
            {props.items.map((item, i) => (
            <li key={i}>
            <label className={ item.done ? 'completed' : null }>
                <input
                type="checkbox"
                readOnly
                checked={ item.done }
                onChange={ () => props.toggle(item) } />
                { item.title }
            </label>
            </li>))}
        </ul>
    )
}
```

Naturally, once again the next component to work on is the _App_. Remember to also copy over and import the same style file which was used by the _riot_ app

```js
import { useEffect } from 'react';
import TodoForm from './components/TodoForm';
import TodoItems from './components/TodoItems'; 
import './assets/style.css';

const remoteUrl = "http://localhost:3030";;

function App(props) {

  useEffect(() => {
    initialTodos();
  }, []);

  function add(title) {
    console.log("not yet wired up");
  }

  function toggle(item) {
    console.log("not yet wired up");
  }
  
  function initialTodos(){
    fetch(`${remoteUrl}/todos`)
    .then(res => res.json())
    .then(props.initTodos);
  }

  return (
    <>
      <h3>{props.title}</h3>
      <TodoForm count={items.length + 1} add={add}></TodoForm>
      <TodoItems items={items} toggle={toggle}></TodoItems>
    </>
  )
}

export default App
```

At this point, the _App_ just needs to be initialized with the right data hook ups. This is the point where reactivity of components relative to their internal state starts to show the seperation between _Riot_ and _React_. To get started, the _main.jsx_ file need to be updated with mock data for now.

```js
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx';

const mockItems = [{title: 'bake bread', done: false}, {title: 'read novela', done: true}]

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App title={'My fab todos'} items={mockItems} />
  </React.StrictMode>,
)
```

Time to light up the _React_ app: ```npm run dev```. Remember to start the _nodejs backend_ server ```PORT=3030 npm start```. Note here that the port number is used to override the default, if the need arises. Just remember to have the port match the _const remoteUrl_ defined in _App.jsx_.

Now, I will dial back the _App.jsx_ component to use it's own internal state for _todo items_. Then I will move that state progressively out of the component, and supply the same state via a _Context_ object.

```js
import { useEffect, useState } from 'react';
import TodoForm from './components/TodoForm';
import TodoItems from './components/TodoItems';
import './assets/style.css';

const remoteUrl = "http://localhost:3030";

function App(props) {

  const [items, setItems] = useState([])

  useEffect(() => {
    initialTodos();
  }, []);

  function add(title) {
    setItems(state => [...state, { title, done: false }])
  }

  function toggle(item) {
    setItems((state) => ([
      ...state.map(s => {
        if (s.title === item.title) {
          return ({ ...s, done: !s.done });
        }
        return s;
      })]))
  }

  function initialTodos() {
    fetch(`${remoteUrl}/todos`)
      .then(res => res.json())
      .then(props.initTodos);
  }

  return (
    <>
      <h3>{props.title}</h3>
      <TodoForm count={items.length + 1} add={add}></TodoForm>
      <TodoItems items={items} toggle={toggle}></TodoItems>
    </>
  )
}

export default App
```

This is the point where opinions start to form when working with _React_, depending on the person whom you talk to. Some prefer to use the _Redux style_ state management. Some will swear by _React's_ own _Context_ API. Some want state management outside of React, like _Zustand_, _Jotai_ etc. For this exercise however, I will use _React's_ own _Context_ API and simply cut off external dependencies. This is not an endorsment of this approach. This is just convenient approach to use becasue it's already available.

Create and export the _AppContext_ from _react/store/index.js_. Allow me to quickly mention the main pieces.

- _AppContext_ is a wrapper of the _application state_, in _React_ parlance, that is. It's an object which has a property called _Provider_, which is initialized with some _application state_, and it then becomes for other components in the application to consume.  
- _useAppContext_ is a function used to retrieve the _AppContext_ values in any component that needs to use that _application state_ values. The alternative is to wrap the coponent needing the _application state_ inside another one of AppContext's property called _Consumer_, I think the first approach (_useAppContext())_ is a tad bit cleaner, but that's just an opinion.
- _AppContextProvider_ is a _React_ component which wraps the _application state_ together with all the methods that manupulate that state, and then passes these values of to the _AppContext.Provider_.

```js
import { createContext, useContext, useState } from 'react';

const remoteUrl = "http://localhost:3030";

const AppContext = createContext(null);

export function useAppContext() {
    return useContext(AppContext)
}

export function AppContextProvider({ children }) {

    const [items, setItems] = useState([])

    function add(title) {
        setItems(state => [...state, { title, done: false }])
    }

    function toggle(item) {
        setItems((state) => ([
            ...state.map(s => {
                if (s.title === item.title) {
                    return ({ ...s, done: !s.done });
                }
                return s;
            })]))
    }

    function initialTodos() {
        fetch(`${remoteUrl}/todos`)
            .then(res => res.json())
            .then(setItems);
    }

    return (
        <AppContext.Provider value={{
            title: 'My fav todos',
            items,
            add,
            toggle,
            initialTodos,
        }}>
            {children}
        </AppContext.Provider>);
}
```

With this out of the way, the _App_ component in _index.jsx_ needs to be wrapped inside the _AppContextProvider_, as required by _React_.

```js
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx';
import { AppContextProvider, } from './store/index';

ReactDOM.createRoot(document.getElementById('root')).render(
  <AppContextProvider>
    <React.StrictMode>
      <App />
    </React.StrictMode>
  </AppContextProvider>,
)
```

You will immediately notice that there are no props being passed off to the _App_ component here. That's because all the state is now available for use at the place where it is needed - _props drilling_ is no longer necessary. The _App_ component now will look a lot different, a lot _React-like_

```js
import { useEffect, } from 'react';
import TodoForm from './components/TodoForm';
import TodoItems from './components/TodoItems';
import { useAppContext } from './store';
import './assets/style.css';

function App() {

  const { title, items, add, toggle, initialTodos } = useAppContext();

  useEffect(() => {
    initialTodos();
  }, []);

  return (
    <>
      <h3>{title}</h3>
      <TodoForm count={items.length + 1} add={add}></TodoForm>
      <TodoItems items={items} toggle={toggle}></TodoItems>
    </>
  )
}

export default App

```

The _App_ continues to work as expected. The _application state_ is no longer inside the _App_ component, but is now moved off into a seperate component whose role is purely to manage that state. 

### Connect to remote server (Redis state)

But now this application needs to be connected to _Redis_ for getting _application state_ updates, just like the _riot_ app from the past section did. First thing is to use use the fetch API in the AppContext

Create a new file _react/store/events.jsx_ and copy over the _startEvents_ function from the _riot_ example, and make just a slight change in the function arguments.

```bash
touch react/store/events.jsx

export function startEvents({ remoteUrl, addTodo, toggleTodos }) {
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
        addTodo(data);
    });

    sub.addEventListener('toggle', (event) => {
        const data = JSON.parse(event.data);
        toggleTodos(data);
    });
}
```

This will receive events from the backend whenever a change is published by _Redis_, just like in the _riot_ example. The _AppContext_ now needs to change as well, so that it may make updates to the _application state_ using a different set of triggers.

```js
import { createContext, useContext, useState } from 'react';
import { startEvents } from './events';

const remoteUrl = "http://localhost:3030";

const AppContext = createContext(null);

export function useAppContext() {
    return useContext(AppContext)
}

export function AppContextProvider({ children }) {

    const [items, setItems] = useState([])

    function add(title) {
        // setItems(state => [....])
        fetch(`${remoteUrl}/todos/${title}`, { method: 'POST', })
                // .then(res => res.json())
                .then(data => {
                  console.log('add todo status', data);
                })
    }

    function toggle(item) {
        // setItems((state) => ([...]))
        fetch(`${remoteUrl}/todos/${item.title}`, { method: 'PUT', })
                // .then(res => res.json())
                .then(data => {
                  console.log('toggle todo status', data);
                })
    }

    function initialTodos() {
        fetch(`${remoteUrl}/todos`)
            .then(res => res.json())
            .then(setItems);
    }

    function addTodo (todo) {
        setItems((state) => ([...state, todo]));
    }

    function toggleTodos (todos) {
        setItems(todos);
    }

    //fire up events listener
    startEvents({remoteUrl, addTodo, toggleTodos});

    return (
        <AppContext.Provider value={{
            title: 'My fav todos',
            items,
            add,
            toggle,
            initialTodos,
            addTodo,
            toggleTodos,
        }}>
            {children}
        </AppContext.Provider>);
}
```

Everything else remains unaltered, and the application is now back to using a _Redis_ backend and a _React_ frontend.

Conclusion

- Although _React_ gives you a lot of ways you can manage an application's state, I think this is actually a source of confusion and also a source of unproductive and unending arguments. I think given the choice, I'd personally not use _React_ for my projects simply because of the cognitive overhead it adds to doing things that are supposed to be just simple.
- I think that with every move the _React_ team makes to address a shortcoming with _React_, the solutions they come up with always seem to propose a radically different way of doing the same things. The level of complexity and shifting paradigms keeps growing with each iteration, instead of getting simplified.
- With that said, the _React_ community is absolutely and fervently passionate about the framework, and _React_ is definitely here to stay