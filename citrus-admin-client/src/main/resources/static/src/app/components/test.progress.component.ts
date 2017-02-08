import {Component,  Input} from '@angular/core';
import {LoggingOutput} from "../model/logging.output";
import {TestResult} from "../model/tests";

@Component({
    selector: "test-progress",
    template: `<div class="progress">
  <div [class]="failed ? 'progress-bar progress-bar-danger' : completed == 100 ? 'progress-bar progress-bar-success' : 'progress-bar progress-bar-primary'"
                role="progressbar" [attr.aria-valuenow]="completed" aria-valuemin="0" aria-valuemax="100" [style.width.%]="completed" [textContent]="failed ? 'Failed' : completed + '%'"></div>
</div>`
})
export class TestProgressComponent {

    @Input() completed: number;
    @Input() failed: boolean;
}