## Zustand - state management

_[Zustand](https://github.com/pmndrs/zustand)_ is a bare-bone state-management solution which uses simplified _flux_ principles, and can be used in different frameworks, or in standalone mode. For this exercise, I will use it in standalone mode to provide the data backing necessary for the _todo-list_ app

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

- _add_ and _toggle_ no longer manipulate state directly anymore. They instead delegate their original roles to the _add_ and _toggle_ functions in _zustand_'s store, respectively.
- three (3) lifecycle method for _riot_ components now come into play - _onBeforeMount_, _onMounted_ and _onMounted_. They are used to (1) define the initial state of the component's, (2) latch onto the store's _subscribe_ function and (3) _unsubscribe_ from the store, respectively. When the state of the store changes, this triggers the callback in the _subscribe_ method, and at this point, the component can decide on how to act on that information. In this scenario, the desired effect is simply to re-render the list component.
- the _todos_ state can now be used in other places, and _not just_ in the _app_ component.
- in the component's _update({...})_ function, one can optionally pass a map of values which the component is interested in detecting whether a change has occurred, so that the re-rendering only happens for a specific subset of state properties (this reduces the footprint of re-renders, which makes the process a lot more controlled and efficient). 
