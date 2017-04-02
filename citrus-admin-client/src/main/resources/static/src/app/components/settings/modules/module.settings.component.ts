import {Component, OnInit} from '@angular/core';
import {ProjectService} from "../../../service/project.service";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {Module} from "../../../model/module";

@Component({
    templateUrl: 'module-settings.html'
})
export class ModuleSettingsComponent implements OnInit {

    constructor(private _projectService: ProjectService,
                private _alertService: AlertService) {
    }

    modules: Module[] = [];

    ngOnInit() {
        this.getModules();
    }

    getModules() {
        this._projectService.getModules()
            .subscribe(
                modules => {
                    this.modules = modules;
                },
                error => this.notifyError(<any>error));
    }

    addModule(module: Module) {
        module.active = true;
        this._projectService.updateModule(module)
            .subscribe(response => this.notifySuccess("Successfully added module" + module.name),
                error => this.notifyError(<any>error));
    }

    removeModule(module: Module) {
        module.active = false;
        this._projectService.updateModule(module)
            .subscribe(response => this.notifySuccess("Successfully removed module " + module.name),
                error => this.notifyError(<any>error));
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }

}