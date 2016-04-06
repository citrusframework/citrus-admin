/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

// require
var gulp = require('gulp');
var sass = require('gulp-sass');
var ts = require('gulp-typescript');
var tsProject = ts.createProject('tsconfig.json',
    {typescript: require('typescript')});
var del = require('del');

// vars
var outputDir = 'target/classes/static/';

// lib copy
gulp.task('sourcecopy', function() {
    // clean dest
    del([outputDir + 'js/lib/**/*',
        outputDir + 'js/**/*.js',
        outputDir + 'css/**/*',
        outputDir + 'fonts/**/*'], {force: true});

    // copy css sources
    gulp.src('./bower_components/bootstrap/css/bootstrap.min.css')
        .pipe(gulp.dest(outputDir + 'css'));
    gulp.src('./bower_components/bootstrap/css/bootstrap.min.css.map')
        .pipe(gulp.dest(outputDir + 'css'));
    gulp.src('./bower_components/font-awesome/css/font-awesome.min.css')
        .pipe(gulp.dest(outputDir + 'css'));
    gulp.src('./bower_components/font-awesome/css/font-awesome.min.css.map')
        .pipe(gulp.dest(outputDir + 'css'));
    gulp.src('./bower_components/jquery-ui/themes/base/jquery-ui.min.css')
        .pipe(gulp.dest(outputDir + 'css'));

    gulp.src('./bower_components/font-awesome/fonts/**/*')
        .pipe(gulp.dest(outputDir + 'fonts'));

    gulp.src('./src/app/**/*.js')
        .pipe(gulp.dest(outputDir + 'js'));

    // copy bower dependencies
    gulp.src('./bower_components/jquery/dist/jquery.min.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./bower_components/jquery-ui/jquery-ui.min.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./bower_components/underscore/underscore.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./bower_components/moment/moment.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./bower_components/bootstrap/js/bootstrap.min.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));

    // copy angular dependencies
    gulp.src('./node_modules/es6-shim/es6-shim.min.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/angular2/bundles/angular2.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/angular2/bundles/angular2-polyfills.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/angular2/bundles/http.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/angular2/bundles/router.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/systemjs/dist/system.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/systemjs/dist/system-polyfills.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/rxjs/bundles/Rx.min.js')
        .pipe(gulp.dest(outputDir + 'js/lib'));

    gulp.src('./node_modules/es6-shim/es6-shim.map')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/systemjs/dist/system.js.map')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/systemjs/dist/system-polyfills.js.map')
        .pipe(gulp.dest(outputDir + 'js/lib'));
    gulp.src('./node_modules/rxjs/bundles/Rx.min.js.map')
        .pipe(gulp.dest(outputDir + 'js/lib'));
})

// html copy
gulp.task('htmlcopy', function() {
    // clean dest
    del([outputDir + 'index.html',
        outputDir + 'js/**/*.html'], {force:true});

    // copy index
    gulp.src('./index.html')
        .pipe(gulp.dest(outputDir));

    // copy angular templates
    gulp.src('./src/app/**/*.html')
        .pipe(gulp.dest(outputDir + 'js'));
});

// html watch
gulp.task('htmlw', function() {
    // watch index
    gulp.watch('./index.html', ['htmlcopy']);

    // watch angular templates
    gulp.watch('./src/app/**/*.html', ['htmlcopy']);
});

// sass compile
gulp.task('sass', function() {
    gulp.src('./src/css/**/*')
        .pipe(gulp.dest(outputDir + 'css'));

    // compile sass and copy
    return gulp.src('./src/sass/*.scss')
        .pipe(sass.sync().on('error', sass.logError))
        .pipe(gulp.dest(outputDir + 'css'));
});

// sass watch compile
gulp.task('sassw', function() {
    gulp.watch('./src/sass/*.scss', ['sass']);
    gulp.watch('./src/css/*.css', ['sass']);
});

// typescript compile
gulp.task('tsc', function() {
    // compile typescript
    var tsResult = gulp.src('src/app/**/*.ts').pipe(ts(tsProject));

    // copy
    return tsResult.js.pipe(gulp.dest(outputDir + 'js'));
});

// typescript watch compile
gulp.task('tscw', function() {
    gulp.watch(['./src/app/**/*.ts',
            './src/app/**/*.html'],
        ['htmlcopy', 'tsc']);
});

// build sass and ts, copy libs, copy html
gulp.task('build', ['sourcecopy', 'htmlcopy', 'tsc', 'sass']);

// watch sass, ts, and html
gulp.task('watch', ['sassw', 'htmlw', 'tscw']);

// default
gulp.task('default', ['build']);