import {Component} from "@angular/core";
import {ProjectService} from "../../../../service/project.service";
import {Project} from "../../../../model/project";
import {ProjectSettingBase} from "../ProjectSettingBase";
import {AlertService} from "../../../../service/alert.service";

@Component({
    selector: 'sources',
    templateUrl: 'sources.html'
})
export class SourcesComponent extends ProjectSettingBase {

    constructor(
        private projectService:ProjectService,
        private alertService:AlertService
    ) {
        super(projectService, alertService)
    }
}