import {createStore} from 'zustand/vanilla'

export const store = createStore((set) => ({
    todos: [],
    add: (title) => set((state) => ({...state, todos: [...state.todos, {title, done: false}]})),
    toggle: (item) => set((state) => ({
        ...state, todos: state.todos.map(s => {
            if (s.title === item.title) {
                return ({...s, done: !s.done});
            }
            return s;
        })
    })),
    initTodos: (todos) => set((state) => ({...state, todos: [...state.todos, ...todos]})),
    addTodo: (todo) => set((state) => ({...state, todos: [...state.todos, todo]})),
    toggleTodos: (todos) => set((state) => ({...state, todos})),
}))