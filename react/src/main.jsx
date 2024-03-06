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
