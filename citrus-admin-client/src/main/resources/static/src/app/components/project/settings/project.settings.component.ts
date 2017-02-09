import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, NavigationStart} from '@angular/router';
import {Project} from "../../../model/project";
import {ProjectService} from "../../../service/project.service";
import {BuildProperty} from "../../../model/build.property";
import {AlertService} from "../../../service/alert.service";
import {Alert} from "../../../model/alert";

declare var jQuery:any;
declare var _:any;

@Component({
    templateUrl: 'project-settings.html'
})
export class ProjectSettingsComponent implements OnInit {

    constructor(private _projectService: ProjectService,
                private _alertService: AlertService,
                private route: ActivatedRoute,
                private router:Router
    ) {

        let activeTabParam: string = route.snapshot.params['activeTab'];
        if (activeTabParam != null) {
            this.active = activeTabParam;
        }
    }

    menuEntries = [
        {name: 'Project', link:['project']},
        {name: 'Connector', link:['connector']},
        {name: 'Sources', link:['sources']},
        {name: 'Build', link:['build']}
    ]

    active = 'project';
    project: Project = new Project();

    useCustomCommand: boolean = false;
    dialogOpen: boolean = false;

    propertyName: string;
    propertyValue: string;

    ngOnInit() {
        this.getProject();
        this.router.events
            .startWith(new NavigationStart(999, '/project/settings'))
            .filter(e => e instanceof NavigationStart)
            .filter(e => e.url === '/project/settings')
            .subscribe(() => this.router.navigate(['project/settings/project']))
    }

    getProject() {
        this._projectService.getActiveProject()
            .subscribe(
                project => {
                    this.project = project;
                    if (this.project.settings.build.command) {
                        this.useCustomCommand = true;
                    }
                },
                error => this.notifyError(<any>error));
    }

    isActive(name:string) {
        return this.router.isActive(`/project/settings/${name}`, false)
    }

    addProperty() {
        var property = new BuildProperty();
        property.name = this.propertyName;
        property.value = this.propertyValue;
        this.project.settings.build.properties.push(property);

        this.propertyName = "";
        this.propertyValue = "";
    }

    removeProperty(property: BuildProperty, event:MouseEvent) {
        this.project.settings.build.properties.splice(this.project.settings.build.properties.indexOf(property), 1);
        event.stopPropagation();
        return false;
    }

    saveSettings() {
        if (!this.useCustomCommand) {
            this.project.settings.build.command = "";
        }

        this._projectService.update(this.project)
            .subscribe(response => this.notifySuccess("Settings successfully saved"),
                error => this.notifyError(<any>error));
    }

    openDialog() {
        jQuery('#dialog-connector').modal();
    }

    manageConnector() {
        if (this.project.settings.connectorActive) {
            this._projectService.removeConnector()
                .subscribe(
                    response => {
                        this.project.settings.useConnector = false;
                        this.project.settings.connectorActive = false;
                    },
                    error => this.notifyError(<any>error));
        } else {
            this._projectService.addConnector()
                .subscribe(response => {
                        this.project.settings.useConnector = true;
                        this.project.settings.connectorActive = true;
                    },
                    error => this.notifyError(<any>error));
        }

        this.getProject();
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}