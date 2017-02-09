import {Component} from "@angular/core";
import {ProjectService} from "../../../../service/project.service";
import {Project} from "../../../../model/project";
@Component({
    selector: 'connector',
    templateUrl: 'connector.html'
})
export class ConnectorComponent {

    project:Project = new Project();

    constructor(
        private projectService:ProjectService
    ) {}

    ngOnInit() {
        this.projectService.getActiveProject().subscribe(p => this.project = p)
    }

}