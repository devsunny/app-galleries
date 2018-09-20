// We are using node's native package 'path'
// https://nodejs.org/api/path.html
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin'); //  -> ADDED IN THIS STEP

// Constant with our paths
const paths = {  
  SRC: path.resolve(__dirname, 'src/public'),
  JS: path.resolve(__dirname, 'src/js'),
  DIST: path.resolve(__dirname, '../../main/resources/templates'),
  STATICDIST: path.resolve(__dirname, '../../main/resources/static'),
};

// Webpack configuration
module.exports = {
  entry: path.join(paths.JS, 'index.js'),
  output: {
    path: paths.STATICDIST,
    filename: 'index.bundle.js',
    publicPath: '/',
  }, 
  plugins: [
    new HtmlWebpackPlugin({
      template: path.join(paths.SRC, 'index.html'),
      filename: '../templates/index.html',
    }),
    new ExtractTextPlugin('style.bundle.css'), 
  ],  
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: [
          'babel-loader',
        ],
      },     
      {
      	test: /\.scss$/,
          use:[
          	"style-loader",
          	"css-loader",
          	"sass-loader"
          ]
       },     
      {
        test: /\.(png|jpg|gif)$/,
        use: [
          'url-loader',
        ],
      },
    ],
  },
  resolve: {
    extensions: ['.js', '.jsx'],
  },
  devServer: {   
    port: 9000
  }
};