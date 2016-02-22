import {Component,  Input, Output, EventEmitter} from 'angular2/core';
import {TestAction} from "../../model/tests";
import {TestActionComponent} from "./test.action.component";
import {TestTransitionComponent} from "./test.transition.component";

@Component({
    selector: "test-designer",
    template: `<div class="test-actions">
      <div class="start-action">
        <i class="fa fa-play"></i>
      </div>

      <div *ngFor="#action of actions">
        <test-transition></test-transition>
        <test-action [action]="action" (selected)="onActionSelected($event)"></test-action>
      </div>

      <test-transition></test-transition>

      <div class="stop-action">
        <i class="fa fa-stop"></i>
      </div>
    </div>`,
    directives: [TestActionComponent,  TestTransitionComponent]
})
export class TestDesignerComponent {

    @Input() actions: TestAction[];

    selectedAction: TestAction;

    constructor() {}

    onActionSelected(action: TestAction) {
        this.selectedAction = action;
    }
}