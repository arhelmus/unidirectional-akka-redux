require('babel-register')

const gulp = require('gulp')
const uglify = require('gulp-uglify')
const mocha = require('gulp-mocha')
const serve = require('gulp-serve')
const browserify = require('gulp-browserify')

gulp.task('compile', () => {
  return gulp.src('./src/app.es6')
          .pipe(browserify({
            transform: ['babelify'],
            extensions: ['.es6']
          }))
          .pipe(uglify())
          .pipe(gulp.dest('dist'))
})

gulp.task('test', () => {
  return gulp.src('./test/*.es6', {read: false})
          .pipe(mocha({reporter: 'nyan'}))
})

gulp.task('build', ['test', 'compile'])

gulp.task('serve', serve('dist'))

gulp.task('default', ['serve'], () => {
  gulp.watch('./src/*', ['build'])
  gulp.watch('./test/*', ['test'])
})
