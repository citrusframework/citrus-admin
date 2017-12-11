import {Component,  Input, Output, EventEmitter} from '@angular/core';
import {TestAction} from "../../../../model/tests";

@Component({
    selector: "test-action",
    template: `<div [class]="action.dirty ? 'test-action dirty' : 'test-action'" (click)="select()" (mouseenter)="focused = true" (mouseleave)="focused = false">
            <a (click)="remove($event)" [hidden]="!focused" name="remove" title="Remove action" class="pull-right"><i class="fa fa-times" style="color: #A50000;"></i></a>
            <i class="fa icon-{{action.type}}"></i>
            <span>{{action.type}}</span>
        </div>`
})
export class TestActionComponent {

    @Input() action: TestAction;

    @Output() selected = new EventEmitter(true);
    @Output() removed = new EventEmitter(true);

    focused = false;

    constructor() {}

    select() {
        this.selected.emit(this.action);
    }

    remove(event: MouseEvent) {
        this.removed.emit(this.action);
        event.stopPropagation();
        return false;
    }

}
