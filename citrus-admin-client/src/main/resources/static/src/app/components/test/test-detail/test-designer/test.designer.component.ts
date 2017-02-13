import {Component,  Input, Output, EventEmitter} from '@angular/core';
import {TestAction} from "../../../../model/tests";

@Component({
    selector: "test-designer",
    template: `<div class="test-actions">
      <div class="start-action">
        <i class="fa fa-play"></i>
      </div>

      <div *ngFor="let action of actions">
        <test-transition></test-transition>
        <test-action *ngIf="action.actions?.length == 0" [action]="action" (selected)="onActionSelected($event)"></test-action>
        <test-container *ngIf="action.actions?.length > 0" [container]="action" (selected)="onActionSelected($event)"></test-container>
      </div>

      <test-transition></test-transition>

      <div class="stop-action">
        <i class="fa fa-stop"></i>
      </div>
    </div>`
})
export class TestDesignerComponent {

    @Input() actions: TestAction[];

    selectedAction: TestAction;

    constructor() {}

    onActionSelected(action: TestAction) {
        this.selectedAction = action;
    }
}