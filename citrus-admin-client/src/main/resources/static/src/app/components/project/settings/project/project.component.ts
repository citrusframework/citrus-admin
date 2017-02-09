import {Component, OnInit} from "@angular/core";
import {ProjectService} from "../../../../service/project.service";
import {Project} from "../../../../model/project";
@Component({
    selector: 'project',
    templateUrl: 'project.html'
})
export class ProjectComponent implements OnInit{

    project:Project = new Project();

    constructor(
        private projectService:ProjectService
    ) {}

    ngOnInit() {
        this.projectService.getActiveProject().subscribe(p => this.project = p)
    }
}