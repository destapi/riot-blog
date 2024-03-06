import { useEffect, } from 'react';
import TodoForm from './components/TodoForm';
import TodoItems from './components/TodoItems';
import { useAppContext } from './store';
import './assets/style.css';

function App() {

  const { 
    title,
    items,
    add,
    toggle,
    initialTodos } = useAppContext();

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
