import {Component,  Input} from 'angular2/core';
import {LoggingOutput} from "../model/logging.output";
import {TestResult} from "../model/tests";

@Component({
    selector: "test-progress",
    template: `<h3>Results</h3>
<div class="progress">
  <div class="progress-bar progress-bar-success" role="progressbar" [attr.aria-valuenow]="completed" aria-valuemin="0" aria-valuemax="100" [style.width.%]="completed" [textContent]="completed + '%'"></div>
</div>`
})
export class TestProgressComponent {

    @Input() completed: number;
}