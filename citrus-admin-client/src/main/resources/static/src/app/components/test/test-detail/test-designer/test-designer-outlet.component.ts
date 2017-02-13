import {Component, OnInit} from "@angular/core";
import {TestService} from "../../../../service/test.service";
import {ActivatedRoute} from "@angular/router";
import {Test, TestDetail} from "../../../../model/tests";
import {Observable} from "rxjs";
import {TestStateService} from "../../test.state";
@Component({
    selector: 'test-designer-outlet',
    template: `
        <div class="space-top-30"></div>
        <test-designer *ngIf="detail|async" [actions]="(detail|async).actions"></test-designer>
    `
})
export class TestDesignerOutletComponent implements OnInit{

    detail:Observable<TestDetail>;

    constructor(
        private testService:TestStateService,
        private activatedRoute:ActivatedRoute
    ){}

    ngOnInit(): void {
        this.detail = this.testService.selectedTestDetail
    }

}