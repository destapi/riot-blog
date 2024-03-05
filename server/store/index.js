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