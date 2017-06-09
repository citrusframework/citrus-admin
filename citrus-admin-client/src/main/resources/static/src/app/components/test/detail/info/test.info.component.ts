import {Component, Input, ChangeDetectionStrategy} from "@angular/core";
import {TestDetail} from "../../../../model/tests";
import {AlertService} from "../../../../service/alert.service";
import {Alert} from "../../../../model/alert";
@Component({
    selector: 'test-info',
    templateUrl: 'test-info.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TestInfoComponent {
    @Input() detail: TestDetail;

    constructor(private alertService:AlertService) {}

    save() {
        this.alertService.add(new Alert("warning", "Not yet implemented ;)", true))
    }
}