import {Component,  Input, Output, EventEmitter} from '@angular/core';
import {TestAction} from "../../../../model/tests";

@Component({
    selector: "test-container",
    template: `<div class="test-container" (click)="select()">
            <span (click)="toggleCollapse()" class="clickable"><i class="fa icon-{{container.type}}"></i>&nbsp;{{container.type}}<i [class]="collapsed ? 'fa fa-chevron-right pull-right' : 'fa fa-chevron-down pull-right'" style="font-size: 14px; margin: 5px 5px;"></i></span>
            <hr>
            <div [hidden]="collapsed">
              <div *ngFor="let action of container.actions">
                <test-transition></test-transition>
                <test-action *ngIf="action.actions?.length == 0" [action]="action" (selected)="onActionSelected($event)"></test-action>
                <test-container *ngIf="action.actions?.length > 0" [container]="action" (selected)="onActionSelected($event)"></test-container>
              </div>
            </div>
        </div>`
})
export class TestContainerComponent {

    @Input() container: TestAction;

    @Output() selected = new EventEmitter(true);

    collapsed = false;

    constructor() {}

    select() {
        this.selected.emit(this.container);
    }

    onActionSelected(action: TestAction) {
        this.selected.emit(action);
    }

    toggleCollapse() {
        this.collapsed = !this.collapsed;
    }

}