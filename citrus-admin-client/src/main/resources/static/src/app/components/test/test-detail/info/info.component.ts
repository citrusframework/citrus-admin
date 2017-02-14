import {Component, OnInit, Input, ChangeDetectionStrategy} from "@angular/core";
import {TestStateService} from "../../test.state";
import {TestDetail} from "../../../../model/tests";
import {Observable} from "rxjs";
import {TestService} from "../../../../service/test.service";
import {AlertService} from "../../../../service/alert.service";
import {Alert} from "../../../../model/alert";
@Component({
    selector: 'info',
    templateUrl: 'info.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InfoComponent {
    @Input() detail:TestDetail
    constructor(
        private testService:TestService,
        private alertService:AlertService
    ) {}

    save() {
        this.alertService.add(new Alert("warning", "Not yet implemented ;)", true))
    }
}

@Component({
    selector: 'info-outlet',
    template: `
        <info [detail]="detail|async"></info>`
})
export class InfoOutletComponent implements OnInit {
    detail:Observable<TestDetail>
    constructor(private testState:TestStateService) {

    }

    ngOnInit(): void {
        this.detail = this.testState.selectedTestDetail;
    }

}