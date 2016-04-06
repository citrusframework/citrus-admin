import {Component, AfterViewInit} from 'angular2/core';

declare var jQuery:any;

@Component({
    selector: 'setup',
    templateUrl: 'js/components/open.project.html'
})
export class SetupComponent implements AfterViewInit {

    constructor() {}

    projectHome: string;

    ngAfterViewInit() {
        jQuery('#file-tree').fileTree({
            root: '/',
            script: 'file/browse',
            multiFolder: false,
            expandSpeed: 1,
            collapseSpeed: 1
        }, function(file) {
            jQuery('input[name="projecthome"]').val(file);
            jQuery('#file-tree').hide();
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
        jQuery('input[name="projecthome"]').val(selected);
        this.close();
    }

    cancel() {
        window.location.href = "/";
    }

    onSubmit() {
        jQuery.ajax({
            url: "project",
            type: 'POST',
            data: encodeURI("projecthome=" + jQuery('input[name="projecthome"]').val()),
            async: false,
            success: function(response) {
            }
        });
        window.location.href = "/";
    }
}