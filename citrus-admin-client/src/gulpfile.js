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
var outputDir = '../target/classes/static/';

// lib copy
gulp.task('sourcecopy', function() {
    // clean dest
    del([outputDir + 'app/lib/**/*',
        outputDir + 'app/**/*.js',
        outputDir + 'app/images/**/*',
        outputDir + 'app/css/**/*',
        outputDir + 'app/fonts/**/*'], {force: true});

    gulp.src('./app/images/**/*')
        .pipe(gulp.dest(outputDir + 'app/images'));

    // copy css sources
    gulp.src('./bower_components/bootstrap/css/bootstrap.min.css')
        .pipe(gulp.dest(outputDir + 'app/css'));
    gulp.src('./bower_components/bootstrap/css/bootstrap.min.css.map')
        .pipe(gulp.dest(outputDir + 'app/css'));
    gulp.src('./bower_components/font-awesome/css/font-awesome.min.css')
        .pipe(gulp.dest(outputDir + 'app/css'));
    gulp.src('./bower_components/font-awesome/css/font-awesome.min.css.map')
        .pipe(gulp.dest(outputDir + 'app/css'));
    gulp.src('./bower_components/jquery-ui/themes/base/jquery-ui.min.css')
        .pipe(gulp.dest(outputDir + 'app/css'));

    gulp.src('./bower_components/font-awesome/fonts/**/*')
        .pipe(gulp.dest(outputDir + 'app/fonts'));

    gulp.src('./app/**/*.js')
        .pipe(gulp.dest(outputDir + 'app'));

    // copy bower dependencies
    gulp.src('./bower_components/jquery/dist/jquery.min.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./bower_components/jquery-ui/jquery-ui.min.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./bower_components/underscore/underscore.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./bower_components/moment/moment.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./bower_components/bootstrap/js/bootstrap.min.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));

    // copy angular dependencies
    gulp.src('./node_modules/es6-shim/es6-shim.min.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/angular2/bundles/angular2.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/angular2/bundles/angular2-polyfills.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/angular2/bundles/http.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/angular2/bundles/router.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/systemjs/dist/system.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/systemjs/dist/system-polyfills.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/rxjs/bundles/Rx.min.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/stompjs/lib/stomp.js')
        .pipe(gulp.dest(outputDir + 'app/lib'));

    gulp.src('./node_modules/es6-shim/es6-shim.map')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/systemjs/dist/system.js.map')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/systemjs/dist/system-polyfills.js.map')
        .pipe(gulp.dest(outputDir + 'app/lib'));
    gulp.src('./node_modules/rxjs/bundles/Rx.min.js.map')
        .pipe(gulp.dest(outputDir + 'app/lib'));
})

// html copy
gulp.task('htmlcopy', function() {
    // clean dest
    del([outputDir + 'index.html',
        outputDir + 'setup.html',
        outputDir + 'app/**/*.html'], {force:true});

    // copy index
    gulp.src('./index.html')
        .pipe(gulp.dest(outputDir));
    gulp.src('./setup.html')
        .pipe(gulp.dest(outputDir));

    // copy angular templates
    gulp.src('./app/**/*.html')
        .pipe(gulp.dest(outputDir + 'app'));
});

// html watch
gulp.task('htmlw', function() {
    // watch index
    gulp.watch(['./index.html',
        './setup.html'], ['htmlcopy']);

    // watch angular templates
    gulp.watch('./app/**/*.html', ['htmlcopy']);
});

// sass compile
gulp.task('sass', function() {
    gulp.src('./app/css/**/*')
        .pipe(gulp.dest(outputDir + 'app/css'));

    // compile sass and copy
    return gulp.src('./app/sass/*.scss')
        .pipe(sass.sync().on('error', sass.logError))
        .pipe(gulp.dest(outputDir + 'app/css'));
});

// sass watch compile
gulp.task('sassw', function() {
    gulp.watch(['./app/sass/*.scss',
        './app/css/*.css'], ['sass']);
});

// typescript compile
gulp.task('tsc', function() {
    // compile typescript
    var tsResult = gulp.src('app/**/*.ts').pipe(ts(tsProject));

    tsResult.js.pipe(gulp.dest('app'));

    // copy
    return tsResult.js.pipe(gulp.dest(outputDir + 'app'));
});

// typescript watch compile
gulp.task('tscw', function() {
    gulp.watch(['./app/**/*.ts',
            './app/**/*.html'], ['tsc']);
});

// build sass and ts, copy libs, copy html
gulp.task('build', ['sourcecopy', 'htmlcopy', 'tsc', 'sass']);

// watch sass, ts, and html
gulp.task('watch', ['sassw', 'htmlw', 'tscw']);

// default
gulp.task('default', ['build']);