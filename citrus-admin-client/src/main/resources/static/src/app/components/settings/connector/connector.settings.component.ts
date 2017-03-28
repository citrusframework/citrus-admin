import {Component} from "@angular/core";
import {Project} from "../../../model/project";
import {ProjectService} from "../../../service/project.service";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";

@Component({
    templateUrl: 'connector-settings.html'
})
export class ConnectorSettingsComponent {

    constructor(private _projectService: ProjectService,
                private _alertService: AlertService) {
    }

    project: Project = new Project();

    ngOnInit() {
        this.getProject();
    }

    getProject() {
        this._projectService.getActiveProject()
            .subscribe(
                project => {
                    this.project = project;
                },
                error => this.notifyError(<any>error));
    }

    openDialog() {
        (jQuery('#dialog-connector') as any).modal();
    }

    manageConnector() {
        if (this.project.settings.connectorActive) {
            this._projectService.removeConnector()
                .subscribe(
                    () => {
                        this.project.settings.useConnector = false;
                        this.project.settings.connectorActive = false;
                    },
                    error => this.notifyError(<any>error));
        } else {
            this._projectService.addConnector()
                .subscribe(() => {
                        this.project.settings.useConnector = true;
                        this.project.settings.connectorActive = true;
                    },
                    error => this.notifyError(<any>error));
        }

        this.getProject();
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }

}