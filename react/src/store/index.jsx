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
        // setItems(state => [...state, { title, done: false }])
        fetch(`${remoteUrl}/todos/${title}`, { method: 'POST', })
                // .then(res => res.json())
                .then(data => {
                  console.log('add todo status', data);
                })
    }

    function toggle(item) {
        // setItems((state) => ([
        //     ...state.map(s => {
        //         if (s.title === item.title) {
        //             return ({ ...s, done: !s.done });
        //         }
        //         return s;
        //     })]))
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