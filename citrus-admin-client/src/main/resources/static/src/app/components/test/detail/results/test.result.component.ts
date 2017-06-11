import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {TestResult, TestDetail, Test} from "../../../../model/tests";
import {ReportService} from "../../../../service/report.service";
import {Alert} from "../../../../model/alert";
import {AlertService} from "../../../../service/alert.service";

@Component({
    selector: "test-result",
    template: `<div class="table-responsive">
  <h2 *ngIf="!detail?.result"><i class="fa fa-file-text-o"></i> No results found for test</h2>
  <div *ngIf="detail?.result" [class]="detail?.result.success ? 'color-success' : 'color-danger'">
      <h2><i class="fa fa-file-text-o"></i> {{detail?.name}}: &nbsp;&nbsp;&nbsp;<span [textContent]="detail?.result.success ? 'SUCCESS' : 'FAILED'"></span></h2>
      <div *ngIf="detail?.result.errorCause" class="error-details">
        <h3 [textContent]="detail?.result.errorCause"></h3>
        <h4 [textContent]="detail?.result.errorMessage"></h4>
        <pre [textContent]="detail?.result.stackTrace"></pre>
      </div>
  </div>
</div>`
})
export class TestResultComponent {

    @Input() detail: TestDetail;
}