import {Component} from "@angular/core";
import {ProjectService} from "../../../../service/project.service";
import {ProjectSettingBase} from "../ProjectSettingBase";
import {AlertService} from "../../../../service/alert.service";
@Component({
    selector: 'build',
    templateUrl: 'build.html'
})
export class BuildComponent extends ProjectSettingBase{
    constructor(
        private projectService:ProjectService,
        private alertService:AlertService
    ) {
        super(projectService, alertService)
    }
}