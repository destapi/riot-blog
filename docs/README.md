# Riot, Redis and Async

[Riot.js](https://riot.js.org/) is a tiny and reactive web component library which is simply refreshing to use

1. It's easy to be up and running pretty quickly
2. Efficient re-rendering using its reactive state capability

The motivation for using Riot js (instead of the common staples like _React_ or _Angular_) is to reduce the tendency to approach this discussion with a pre-set mindset.
A fresh set of tools will sometimes force someone out of their comfort zone, which is the hope here. This discussion is also not a tutorial for any framework or tools, 
even though it might read like one, so I will not be spending too much time explaining concepts unless it is to articulate a point more clearly.

The goal here will be to try and move around state management to different parts of the application stack, and try to understand how the choice affects the features that an 
application can support as a result. Traditionally, the first thought in solving a web-based problem is to clobber together a traditional 3-tier architecture and call it a day. 
But that architecture is not necessarily a panacea, and is probably the cause for so many sleepless nights in the industry, because of challenges with capacity, scaling and transactions 
that the design is not able to adequately meet.

## Setting up shop

1. open new folder and start new _npm project_

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

> With Riot, you can optionally compile components directly in the browser, or you can pre-compile them into bundles which the html page can then use. In this exercise, I will be pre-compiling the bundles.

> With Riot again, you can pre-compile bundles using different widely used tools, including Webpack, Rollup, Parcel and Browserify. In this exercise, I will use Webpack simply because of its ubiquity

3. Install necessary dependencies

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

> The _mode: 'development'_ and _devtool: 'inline-source-map'_ properties are _not mandatory_, but for development purposes, it makes viewing and debugging the generated code a lot easier.

5. Add a build step in _package.json_, which can be executed as _npm run build_

```bash
"scripts": {
  "build": "webpack",
  "test": "echo \"Error: no test specified\" && exit 1"
},
```

> You can optionally choose to _watch_ the project directories to auto-compile any changes detected in the source files

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

9. Add a home page template (_index.html_) to attach onto and display the app components

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

10. Configure _html-webpack-plugin_ to generate an _index.html_ file into the __dist__ folder, and also append a _script tag_ with the necessary output _script file_. While doing so, the option to automatically clean up the __dist__ folder should be turned on, so that older versions of generated artifacts are discarded while newer versions are being created.

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

> First, add a __devServer__ configuration in _webpack.config.js_

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
