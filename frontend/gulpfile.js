require('babel-core/register')

const gulp = require('gulp')
const babel = require('gulp-babel')
const uglify = require('gulp-uglify')
const mocha = require('gulp-mocha')
const serve = require('gulp-serve')
const browserify = require('gulp-browserify')

gulp.task('compile', () => {
  return gulp.src('./src/app.es6')
          .pipe(babel({
            presets: ['es2015', 'react']
          }))
          .pipe(browserify())
          .pipe(uglify())
          .pipe(gulp.dest('dist'))
})

gulp.task('test', () => {
  return gulp.src('./test/appTest.es6', {read: false})
          .pipe(mocha({reporter: 'nyan'}))
})

gulp.task('build', ['test', 'compile'])

gulp.task('serve', serve('dist'))

gulp.task('default', ['serve'], () => {
  gulp.watch('./src/*', ['build'])
  gulp.watch('./test/*', ['test'])
})
