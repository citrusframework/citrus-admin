import {Component, AfterViewInit} from 'angular2/core';
import {NgModel, NgIf} from "angular2/common";

declare var jQuery:any;

@Component({
    selector: 'setup',
    templateUrl: 'app/components/open.project.html',
    directives: [ NgModel, NgIf ]
})
export class SetupComponent implements AfterViewInit {

    constructor() {}

    projectHome: string;
    error: any;

    ngAfterViewInit() {
        jQuery('#file-tree').fileTree({
            root: '/',
            script: 'file/browse',
            multiFolder: false,
            expandSpeed: 1,
            collapseSpeed: 1
        }, function(file) {
            jQuery('li.ext_citrus').toggleClass('selected');
        });

        return undefined;
    }

    browse() {
        jQuery('#dialog-file-tree').modal();
    }

    close() {
        jQuery('#dialog-file-tree').modal('hide');
    }

    select() {
        var selected = jQuery('ul.jqueryFileTree li.expanded').last().children('a:first').attr('rel');
        this.projectHome = selected;
        this.close();
    }

    cancel() {
        window.location.href = "/";
    }

    clearError() {
        this.error = undefined;
    }

    onSubmit() {
        jQuery.ajax({
            url: "project",
            type: 'POST',
            data: encodeURI("projecthome=" + this.projectHome),
            success: function(response) {
                window.location.href = "/";
            },
            error: response => {
                this.error = response.responseJSON;
            }
        });
    }
}