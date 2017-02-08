import {Component, OnInit} from '@angular/core';
import {ProjectSetupService} from "../service/project.setup.service";
import {ProjectSettings} from "../model/project";
import * as jQueryVar from 'jquery'

// TODO: C'mon this is a dirty hack to tell TS that jQuery is basically from type jQuery but not the global jQuery will be used...
declare var jQuery:typeof jQueryVar;

@Component({
    selector: 'setup',
    templateUrl: 'setup-project.html'
})
export class SetupComponent implements OnInit {

    constructor(
        private _projectSetupService: ProjectSetupService
    ) {
    }

    projectHome: string;
    settings: ProjectSettings = new ProjectSettings();
    recentlyOpened: string[] = [];
    success: string;
    error: any;

    ngOnInit() {
        (jQuery('#file-tree') as any).fileTree({
            root: '/',
            script: 'api/file/browse',
            multiFolder: false,
            expandSpeed: 1,
            collapseSpeed: 1
        }, function () {
            jQuery('li.ext_citrus').toggleClass('selected');
        });

        this._projectSetupService.getProjectHome()
            .subscribe(
                response => this.projectHome = response.text(),
                error => this.error = error.json());

        this._projectSetupService.getRecentProjects()
            .subscribe(
                response => {
                    this.recentlyOpened = response.json();
                },
                error => this.error = error.json());

        this._projectSetupService.getDefaultProjectSettings()
            .subscribe(
                response => this.settings = response.json(),
                error => this.error = error.json());
    }

    browse() {
        (jQuery('#dialog-file-tree') as any).modal();
    }

    close() {
        (jQuery('#dialog-file-tree') as any).modal('hide');
    }

    showSettings() {
        (jQuery('#dialog-settings') as any).modal();
    }

    hideSettings() {
        (jQuery('#dialog-settings') as any).modal('hide');
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
        if (this.projectHome) {
            this.open(this.projectHome);
        }
    }

    open(projectHome: string) {
        this._projectSetupService.openProject(projectHome)
            .subscribe(
                success => window.location.href = "/",
                error => this.error = error.json());
    }
}