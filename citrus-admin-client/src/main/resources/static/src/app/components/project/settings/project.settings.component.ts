import {Component, OnInit} from '@angular/core';
import {Router, NavigationStart} from '@angular/router';
import {Project} from "../../../model/project";
import {ProjectService} from "../../../service/project.service";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";


@Component({
    templateUrl: 'project-settings.html'
})
export class ProjectSettingsComponent implements OnInit {

    constructor(private _projectService: ProjectService,
                private _alertService: AlertService,
                private router:Router
    ) {
    }

    menuEntries = [
        {name: 'Project', link:['project']},
        {name: 'Connector', link:['connector']},
        {name: 'Sources', link:['sources']},
        {name: 'Build', link:['build']}
    ]

    project: Project = new Project();

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
                },
                error => this.notifyError(<any>error));
    }

    isActive(name:string) {
        return this.router.isActive(`/project/settings/${name}`, false)
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }

}