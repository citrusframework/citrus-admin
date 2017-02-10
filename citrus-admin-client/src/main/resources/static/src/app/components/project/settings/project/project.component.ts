import {Component, OnInit} from "@angular/core";
import {ProjectService} from "../../../../service/project.service";
import {AlertService} from "../../../../service/alert.service";
import {ProjectSettingBase} from "../ProjectSettingBase";

@Component({
    selector: 'project',
    templateUrl: 'project.html'
})
export class ProjectComponent extends ProjectSettingBase {
    constructor(
        private projectService:ProjectService,
        private alertService:AlertService
    ) {
        super(projectService, alertService)
    }
}