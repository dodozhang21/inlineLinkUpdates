'use strict';

module.exports = function(grunt) {

    // Project configuration.
    grunt.initConfig({

        // Metadata.
        pkg: grunt.file.readJSON('package.json'),
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n',

        // Task configuration.
        clean: {
            options: {
                force: true
            },
            files: ['css/']
        },
        compass: {
            dist: {
                options: {
                    //config: 'config.rb'
                    sassDir: 'sass',
                    cssDir: '../resources/static/css',
                    specify: 'sass/*.scss',
                    outputStyle: 'compact'
                }
            }
        },
        bower: {
            install: {
                options: {
                    cleanup: true,
                    targetDir: 'js/libs'
                }
            }
        },
        cssmin: {
            options: {
                banner: '/* DO NOT COMMIT */',
                report: false /* change to 'gzip' to see gzipped sizes on local */
            },
            minify: {
                expand: true,
                cwd: 'css/',
                src: ['**/*.css', '!*.min.css'],
                dest: '../resources/static/css/'
                /*ext: '.css'*/
            }
        },
        watch: {
          sassy: {
            files: ['sass/**/*.scss'],
            tasks: ['compass'],
            options: {
                spawn: false,
                livereload: false
            }
          }
        }
        ,
        browser_sync: {
            files: {
                src : ['css/**/*.css']
            },
            options: {
                watchTask: true
            }
        }
    });

    // These plugins provide necessary tasks.
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-compass');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-browser-sync');
    grunt.loadNpmTasks('grunt-bower-task');

    // Default task.
    grunt.registerTask('local', ['clean', 'compass', 'browser_sync', 'watch']);
    grunt.registerTask('default', ['clean', 'compass', 'cssmin']);


};