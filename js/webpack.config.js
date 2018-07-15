var clean = require('clean-webpack-plugin');
var extractText = require('extract-text-webpack-plugin');
var uglify = require('webpack-uglify-js-plugin');

const path = require('path');

var destPath = path.resolve(path.join('..', 'src', 'main', 'webapp', 'js', 'dist'));

console.log(destPath);

module.exports = {
  entry: './index.js',
  output: {
    filename: 'app.js',
    path: destPath
  },
  module: {
    loaders: [
      {
        test: /\.css$/,
        loader: extractText.extract({ fallback: 'style-loader', use: 'css-loader' })
      },
      {
        test: /\.(jpg|png)$/,
        loader: 'file-loader'
      },
      {
        test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        loader: "url-loader?limit=10000&mimetype=application/font-woff"
      }, {
        test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        loader: "file-loader"
      },
    ]
  },
  plugins: [
    new clean([destPath]),
    new extractText("app.css"),
    new uglify({
      cacheFolder: path.resolve(__dirname, 'cached_uglify'),
      debug: false,
      minimize: true,
      sourceMap: false,
      output: {
        comments: false
      },
      compressor: {
        warnings: false
      }
    })
  ]
};