import * as riot from 'riot'
import App from '../riot/app.riot'
import {store} from './store';

const mountApp = riot.component(App);
const {getState, subscribe} = store;

const app = mountApp(
  document.getElementById('root'),
  { title: "My fav list", getState, subscribe }
)