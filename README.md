# Small but mighty - Riot JS + Zustand

Riot is a tiny and reactive web component library which is simply refreshing to use 

1. Easy to pick up and get running very quickly
2. Efficient re-rendering using its reactive state

## Setting up shop

1. open new folder and start new npm project

```bash
npm init -y
```

2. Set up project structure

```bash
mkdir riot dist public src
touch public/index.html
touch riot/app.riot
touch src/index.js
```

A tree view of the initial project structure

```
C:.
│   package.json
│
├───dist
├───public
|       index.html
├───src
|       index.js
└───riot
        app.riot
```

> With Riot, you can optionally compile components directly in the browser, or you can pre-compile them into bundles which the html page can then use. In this exercise, I will be precompiling the bundles.  

> With Riot, you can precompile bundles using different widely used tools, including Webpack, Rollup, Parcel and Browserify. In this exercise, I will use Webpack simply because of its ubiquity

3. Install neccessary dependencies

```bash
npm i riot
npm i -D @riotjs/compiler @riotjs/webpack-loader 
npm i -D webpack webpack-cli webpack-dev-server 
```

4. Create a simple webpack config file, and configure _@riotjs/webpack-loader_ to detect _.riot_ files and compile them into javascript. The other default configurations for webpack will be sufficient for this exercise

```bash
touch webpack.config.js

module.exports = {
  mode: 'development',
  devtool: 'inline-source-map',
  module: {
    rules: [
      {
        test: /\.riot$/,
        exclude: /node_modules/,
        use: [
          {
            loader: '@riotjs/webpack-loader',
            options: {
              hot: false, // set it to true if you are using hmr
              // add here all the other @riotjs/compiler options riot.js.org/compiler
              // template: 'pug' for example
            },
          },
        ],
      },
    ],
  },
}
```

> The _mode: 'development'_ and _devtool: 'inline-source-map'_ properties are _not mandatory_, but for development purposes, it makes viewing and understanding the generated code a lot easier.  

5. Add a build step in _package.json_, which can be executed as _npm run build_

```bash
"scripts": {
  "build": "webpack",
  "test": "echo \"Error: no test specified\" && exit 1"
},
```

> You can optionally choose to _watch_ the project directories to auto-compile any changes detected to the source files

```bash
"scripts": {
  ...
  "watch": "webpack --watch",
  "build": "webpack",
  ...
},
```

6. Create the first riot component

```html
<app>
  <p>{ props.message }</p>
</app>
```

7. Add an entry point (_index.js_) for the bundler to use for discovering and pre-compiling riot components

```bash
import * as riot from 'riot'
import App from '../riot/app.riot'

const mountApp = riot.component(App)

const app = mountApp(
  document.getElementById('root'),
  { message: 'Hello World', items: [] }
)
```

8. Build the project to view the generated output in _dist_ folder

```bash
npm run build
```

9. Add a home page template (_index.html_) to attach onto and and display the app components

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Riot Demo</title>
</head>
<body>
    <div id="root"></div>
</body>
</html>
```

10. Configure _html-webpack-plugin_ to generate a _index.html_ file into the __dist__ folder, and also append a _script tag_ with the necessary output _script file_. While doing so, the option to automatically clean up the __dist__ folder should be turned on, so that older versions of generated artifacts are discarded while newer versions are being created.

```bash
npm i -D html-webpack-plugin 
```

> Configure html plugin

```bash
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  output: {
    clean: true,
  },
  plugins: [
    new HtmlWebpackPlugin({
        template: './public/index.html'
    }),
    ...
  ],
};
```

11. Configure a _development server_ and fire up a server instance 

> First, add __devServer__ configuration in _webpack.config.js_

```bash
const path = require('path');

module.exports = {
  //...
  devServer: {
    static: './dist',
  },
};
```

> Add a __serve__ command in the _script_ section of _package.json_  

```bash
"scripts": {
  ...
  "serve": "webpack serve",
  ...
},
```

> Now serve the app html by executing the _serve_ script

```bash
npm run serve
```

This will bring up a dev server and serve the _dist_ folder content on port 8080 from your localhost

```bash
webpack 5.90.3 compiled successfully in 1045 ms
<i> [webpack-dev-server] Project is running at:
<i> [webpack-dev-server] Loopback: http://localhost:8080/
<i> [webpack-dev-server] On Your Network (IPv4): http://192.168.1.24:8080/
<i> [webpack-dev-server] On Your Network (IPv6): http://[fe80::4a07:204f:eefa:3914]:8080/       
<i> [webpack-dev-server] Content not from webpack is served from './dist' directory
```

12. Extend the basic example to a simple _todo_ example. Start with an input form component

```bash
touch riot/todo-form.riot
```

Add a basic form and start putting together the features it will need

```html
<todo-form>
  <form onsubmit={ add }>
    <input onkeyup={ edit } value={ state.text } />
    <button disabled={ !state.text }>
      Add #{ props.count }
    </button>
  </form>
</todo-form>
```

> The form will maintain internal state that will reflect a task's name.  
> The form will need _submit_ handler for its input field value, so I'll add an __add__ handler.  
> The form's input field will need a _keyup_ event handler, and for this, I'll add an __edit__ handler.  

```html
<script>
  export default {
    onBeforeMount(props, state) {
      // initial state
      this.state = {
        text: ''
      }
    },
    edit(e) {
      // update only the text state
      this.update({
        text: e.target.value
      })
    },
    add(e) {
      e.preventDefault()

      if (this.state.text) {
        this.props.add(this.state.text)
        this.update({
          text: '',
        });
      }
    },
  }
</script>
```

### key takewaways

- updating a form input element - the _keyup_ event is used to update the internal state of the component to reflect the value in the _input field_. And since the binding is two-way, the value of the _input field_ is set to reflect the value of the component's inner state.

- the value of the form's input field is cleared when the _add_ button is clicked. This is done by setting the internal state of the component to an empty string.

13. The _todo-form_ component is now ready for prime time and can be tested in the browser by adding it to the _<app>_ component as its child. To register any component, like in this case a child component, it can be done inside parent (in this case the __app__) or globally. For this exercise, I will configure the child inside the parent

> Adjust the entry file  

```bash
const app = mountApp(
  document.getElementById('root'),
  { title: "My fav list", items: [] }
)
```

> Adjust the _app_ component to reflect the _todo_ list  

```html
<app>
  <h3>{this.props.title}<h3>
  <todo-form count={this.state.items.length + 1} add={add}></todo-form>

  <script>
    import TodoForm from './todo-form.riot';

    export default {
      components: {
        TodoForm
      },
      onBeforeMount(props, state) {
        // initial state
        this.state = {
          items: props.items,
        }
      },
      add(title) {
          this.update({
            items: [
              ...this.state.items,
              // add a new item
              {title, done: false}
            ],
          })
      },
      toggle(item) {
        item.done = !item.done
        // trigger a component update
        this.update()
      }
    }
  </script>
</app>
```

### key takeways

- importing any component into the application can be accomplished using the _import_ statement
- a component can be registered at the global level, or can be configured as a child of an existing component
- the internal state in this case is an empty array, and the items getting added to it take the form _{title: <title>, done: <true/false>}_ 

14. The add event currently adds values to a list in the _app_ component's internal state, but the items are not reflected anywhere. It's time add another component to interact with the list items

```bash
touch riot/todo-items.riot
```

> Add the component definition  

```html
<todo-items>
  <ul>
    <li each={ item in this.props.items }>
      <label class={ item.done ? 'completed' : null }>
        <input
          type="checkbox"
          checked={ item.done }
          onclick={ () => this.props.toggle(item) } />
        { item.title }
      </label>
    </li>
  </ul>
</todo-items>
```

15. Update the _app_ component to register this new child component, and pass properties to it

```html
<app>
  <h3>{this.props.title}<h3>
  <todo-form count="{this.state.items.length + 1}" add="{add}"></todo-form>
  <todo-items items={state.items} toggle={toggle}></todo-items>

  <script>
    import TodoForm from './todo-form.riot';
    import TodoItems from './todo-items.riot';

    export default {
      components: {
        TodoForm,
        TodoItems,
      },

      ...<rest>...
</app>
```

16. Styling can be added either

- scoped to each component
- higher level styling to affect multiple components

in this case, a little styling can be added to the _todo-list_ via a seperate css file, although this same styling can be added using a _style_ tag right inside the _app_ component. Again, it's purely just a matter of style, convenience and design goals.

> Create the styles file

```bash
touch public/style.css
```

> Link the styles file to the index file template

```html
  <title>Riot Demo</title>
  <link href="style.css" rel="stylesheet" type="text/css" />
```

> Add styling
```css
body {
  font-family: 'myriad pro', sans-serif;
  font-size: 20px;
  border: 0;
}

todo {
  display: block;
  max-width: 400px;
  margin: 5% auto;
}

form input {
  font-size: 85%;
  padding: .4em;
  border: 1px solid #ccc;
  border-radius: 2px;
}

button {
  background-color: #1FADC5;
  border: 1px solid rgba(0,0,0,.2);
  font-size: 75%;
  color: #fff;
  padding: .4em 1.2em;
  border-radius: 2em;
  cursor: pointer;
  margin: 0 .23em;
  outline: none;
}

button[disabled] {
  background-color: #ddd;
  color: #aaa;
}

ul {
  padding: 0;
}

li {
  list-style-type: none;
  padding: .2em 0;
}

.completed {
  text-decoration: line-through;
  color: #ccc;
}

label {
  cursor: pointer;
}
```

17. Configure _copy-webpack-plugin_ to copy the _css_ file into the _dist_folder

```bash
npm i -D copy-webpack-plugin
```

> Configure the plugin inside _webpack-config.js_ file. This is because the _dist_ folder gets cleaned up by the _html-webpack-plugin_ every time a build task is started, and so putting back this file can be automated in this manner.

```bash
const CopyPlugin = require("copy-webpack-plugin");

module.exports = {
  ...
  plugins: [
    ...
    new CopyPlugin({
      patterns: [
        { from: "public/style.css", to: "style.css" },
      ],
    }),
  ],
};
```

### key takaways

- it would still be ok to move the todos list from _app_ to the _todo-items_ component. It's really a matter of style and one's tolerance level to props drilling.
- once again, the key to re-rendering the component is by calling the _this.update({...})_ function when the internal state of the component has changed. I prefer using this explicit trigger to kick off updates, instead of relying on exotic, inference techniques, at least for components where complexity can be avoided.

> So far, the _todo-list_ state is internal to the _app_ component. While this is good for demo purposes, it is not ideal for more complex applications. It's too tightly coupled to the component using it, and so it cannot find any other usage outside of that component. A more ideal scenario is to have the _todo-list_ state exist outside the component using it, and only have events be the only trigger that causes a change to the state. This design can be accomplished by different excellent libraries in existance, and for this exercise, I will use _zustand_

## Zustand - state management

_Zustand_ is a barebone state-management solution which uses simplified _flux_ principles, and can be used in different frameworks, or in standalone mode. For this exercise, I will use it in standalone mode to provide the data backing necessary for the _todo-list_ app

```bash
npm i zustand
```

18. Create a store, and the mutator functions

```bash
touch src/store.js
```

Add content for js file

```js
import { createStore } from 'zustand/vanilla'

export const store = createStore((set) => ({
  todos: [],  
  add: (title) => set((state) => ({...state, todos: [...state.todos, {title, done: false}]})),
  toggle: (item) => set((state) => ({...state, todos: state.todos.map(s => {
      if(s.title === item.title){
        return ({...s, done: !s.done});
      }
      return s;
    })})),
}))
```

> The _todos_ store is pretty straightforward  
- _todos_ is a basic _array_
- _add_ will accept a _title_ and add a new item into the _todos_ array
- _toggle_ will accept an _item_ and use that to flip the _done_ status of a matching todo item in the _todos_ array

19. Switch out internal state in _app_ component to use _zustand_ store

```js
...
import {store} from './store';

const mountApp = riot.component(App);
const {getState, subscribe} = store;

const app = mountApp(
  document.getElementById('root'),
  { title: "My fav list", getState, subscribe }
)
```

> Update the _onBeforeMount_ inside _app.riot_ to resolve the build error (in case you are _watching_ the build)

```bash
onBeforeMount(props, state) {
  // initial state
  this.state = {
    items: props.getState(),
  }
},
```

20. Use the _getState_ function to retrieve the current _todos_ state, and the _subscribe_ function to trigger the _app_ component's _update()_ function whenever a change in the store's data is detected.

```js
<script>
  import TodoForm from './todo-form.riot';
  import TodoItems from './todo-items.riot';

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
      })
    },
    onUnmounted(){
      unsubscribe();
    },
    add(title) {
      this.props.getState().add(title)
    },
    toggle(title) {
      this.props.getState().toggle(title)
    }
  }
</script>
```

### key takeaways

- _add_ and _toggle_ no longer manipulate state directly any more. They instead delegate their original roles to the _add_ and _toggle_ functions in _zustand_'s store, respectively.
- three (3) lifecycle method for _riot_ components now come into play - _onBeforeMount_, _onMounted_ and _onMounted_. They are used to (1) define the initial state of the component's, (2) latch onto the store's _subscribe_ function and (3) _unsubscribe_ from the store, respectively. When the state of the store changes, this triggers the calback in the _subscribe_ method, and at this point, the component can decide on how to act on that information. In this scenario, the desired effect is simply to re-render the list component.
- the _todos_ state can now be used in other places, and _not just_ in the _app_ component.
- in the component's _update({...})_ function, one can optionally pass a map of values which the component is interested in detecting whether a change has occured, so that the re-rendering only happens for a specific subset of state properties (this reduces the footprint of re-renders, which makes the process a lot more controlled and efficient). 