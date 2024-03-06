## The Riot App components

13. Extend the basic example from the last section to a simple _todo_ example. Start with an input form component

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

> The form will need a _submit_ handler for its input field value, so I'll add an __add__ handler.  

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

### key takeaways

- to update a form input element, the _keyup_ event is used to sync up the internal state of the component with the _input field_ value. And since the binding is two-way, the value of the _input field_ is set to reflect the value of the component's inner state.

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

### key takeaways

- importing any component into the application can be accomplished using the _import_ statement
- a component can be registered at the global level, or can be configured as a child of an existing component
- the internal state in this case is an empty array, and the items getting added to it take the form _{title: <title>, done: <true/false>}_

14. The _add_ event currently adds values to a list in the _app_ component's internal state, but the items are not reflected anywhere. It's time add another component to interact with the list items

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

in this case, a little styling can be added to the _todo-list_ via a separate css file, although this same styling can be added using a _style_ tag right inside the _app_ component. Again, it's purely just a matter of style, convenience and design goals.

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

### key takeaways

- it would still be ok to move the todos list from _app_ to the _todo-items_ component. It's really a matter of style and one's tolerance level to props drilling.
- once again, the key to re-rendering the component is by calling the _this.update({...})_ function when the internal state of the component has changed. I prefer using this explicit trigger to kick off updates, instead of relying on exotic, inference techniques, at least for components where complexity can be avoided.
