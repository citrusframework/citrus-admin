import {Component, Input} from '@angular/core';
import {TestDetail} from "../../../../model/tests";

@Component({
    selector: "test-result",
    templateUrl: 'test-result.html'
})
export class TestResultComponent {

    @Input() detail: TestDetail;

    hasResult() {
      return this.detail.result != undefined;
    }
}
