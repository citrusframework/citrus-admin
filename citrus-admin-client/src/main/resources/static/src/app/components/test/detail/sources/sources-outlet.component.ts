import {Component, OnInit} from "@angular/core";
import {TestStateService} from "../../test.state";
import {Observable} from "rxjs";
import {TestDetail} from "../../../../model/tests";
@Component({
    selector: 'sources-outlet',
    templateUrl: 'sources-outlet.html'
})
export class SourcesOutletComponent implements OnInit{

    detail:Observable<TestDetail>
    type:Observable<string>

    constructor(
        private testState:TestStateService
    ) {}

    ngOnInit() {
        this.detail = this.testState.selectedTestDetail;
        this.type = this.detail.map(d => d.type)
    }

}