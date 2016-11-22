import {Component, OnInit} from '@angular/core';
import {ProjectSetupService} from "../service/project.setup.service";
import {ProjectSettings} from "../model/project";

declare var jQuery:any;

@Component({
    selector: 'setup',
    templateUrl: 'app/components/setup-project.html'
})
export class SetupComponent implements OnInit {

    constructor(private _projectSetupService: ProjectSetupService) {
    }

    projectHome: string;
    settings: ProjectSettings = new ProjectSettings();
    success: string;
    error: any;

    ngOnInit() {
        jQuery('#file-tree').fileTree({
            root: '/',
            script: 'file/browse',
            multiFolder: false,
            expandSpeed: 1,
            collapseSpeed: 1
        }, function (file) {
            jQuery('li.ext_citrus').toggleClass('selected');
        });

        this._projectSetupService.getProjectHome()
            .subscribe(
                response => this.projectHome = response.text(),
                error => this.error = error.json());

        this._projectSetupService.getDefaultProjectSettings()
            .subscribe(
                response => this.settings = response.json(),
                error => this.error = error.json());
    }

    browse() {
        jQuery('#dialog-file-tree').modal();
    }

    close() {
        jQuery('#dialog-file-tree').modal('hide');
    }

    showSettings() {
        jQuery('#dialog-settings').modal();
    }

    hideSettings() {
        jQuery('#dialog-settings').modal('hide');
    }

    saveSettings() {
        this._projectSetupService.saveDefaultProjectSettings(this.settings)
            .subscribe(
                success => this.success = "Settings saved successfully",
                error => this.error = error.json());
        this.hideSettings();
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

    clearSuccess() {
        this.success = undefined;
    }

    onSubmit() {
        this._projectSetupService.openProject(this.projectHome)
            .subscribe(
                success => window.location.href = "/",
                error => this.error = error.json());
    }
}