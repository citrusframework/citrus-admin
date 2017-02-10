import {Component} from "@angular/core";
import {ProjectService} from "../../../../service/project.service";
import {AlertService} from "../../../../service/alert.service";
import {ProjectSettingBase} from "../ProjectSettingBase";
@Component({
    selector: 'connector',
    templateUrl: 'connector.html'
})
export class ConnectorComponent extends ProjectSettingBase {
    constructor(
        private projectService:ProjectService,
        private alertService:AlertService
    ) {
        super(projectService, alertService)
    }

}