import {Component, OnInit, Input} from "@angular/core";
import {TestStateService} from "../../test.state";
import {TestDetail} from "../../../../model/tests";
import {Observable} from "rxjs";
@Component({
    selector: 'info',
    templateUrl: 'info.html'
})
export class InfoComponent {

    @Input() detail:TestDetail
}

@Component({
    selector: 'info-outlet',
    template: `<info [detail]="detail|async"></info>`
})
export class InfoOutletComponent implements OnInit {
    detail:Observable<TestDetail>
    constructor(private testState:TestStateService) {

    }

    ngOnInit(): void {
        this.detail = this.testState.selectedTestDetail;
    }

}