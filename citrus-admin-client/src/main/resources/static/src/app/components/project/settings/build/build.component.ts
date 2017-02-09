import {Component} from "@angular/core";
import {ProjectService} from "../../../../service/project.service";
import {Project} from "../../../../model/project";
@Component({
    selector: 'build',
    templateUrl: 'build.html'
})
export class BuildComponent {

    project:Project = new Project();

    constructor(
        private projectService:ProjectService
    ) {}

    ngOnInit() {
        this.projectService.getActiveProject().subscribe(p => this.project = p)
    }
}