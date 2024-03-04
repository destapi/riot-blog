const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyPlugin = require("copy-webpack-plugin");

module.exports = {
  output: {
    clean: true,
  },
  mode: 'development',
  devtool: 'inline-source-map',
  devServer: {
    static: './dist',
  },
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
  plugins: [
    new HtmlWebpackPlugin({
      template: './public/index.html'
    }),
    new CopyPlugin({
      patterns: [
        { from: "public/style.css", to: "style.css" },
      ],
    }),
  ],
}