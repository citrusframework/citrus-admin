import {Component,  Input} from '@angular/core';
import {TestAction, TestDetail} from "../../../../model/tests";
import {Property} from "../../../../model/property";

@Component({
    selector: "test-designer",
    template: `<div class="test-actions fill">
      <div class="start-action">
        <i class="fa fa-play"></i>
      </div>

      <div *ngFor="let action of detail.actions">
        <test-transition></test-transition>
        <test-action *ngIf="action.actions?.length == 0" [action]="action" (selected)="onActionSelected($event)"></test-action>
        <test-container *ngIf="action.actions?.length > 0" [container]="action" (selected)="onActionSelected($event)"></test-container>
      </div>
      
      <div *ngIf="detail.actions.length == 0">
        <test-transition></test-transition>
        <test-action [action]="default"></test-action>
      </div>

      <test-transition></test-transition>

      <div class="stop-action">
        <i class="fa fa-stop"></i>
      </div>
    </div>`
})
export class TestDesignerComponent {

    @Input() detail: TestDetail;

    constructor() {
        this.default = new TestAction();
        this.default.type = "empty";

        let name = new Property();
        name.id = "reason";
        name.value = "Unable to read test model";
        this.default.properties.push(name);
    }

    default: TestAction;
    selectedAction: TestAction;

    onActionSelected(action: TestAction) {
        this.selectedAction = action;
    }
}