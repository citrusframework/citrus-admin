import {Component} from "@angular/core";
import {ProjectService} from "../../../../service/project.service";
import {Project} from "../../../../model/project";
@Component({
    selector: 'sources',
    templateUrl: 'sources.html'
})
export class SourcesComponent {

    project:Project = new Project();

    constructor(
        private projectService:ProjectService
    ) {}

    ngOnInit() {
        this.projectService.getActiveProject().subscribe(p => this.project = p)
    }
}