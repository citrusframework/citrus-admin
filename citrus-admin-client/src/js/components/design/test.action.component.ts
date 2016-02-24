import {Component,  Input, Output, EventEmitter} from 'angular2/core';
import {TestAction} from "../../model/tests";

@Component({
    selector: "test-action",
    template: `<div class="test-action" (click)="select()" (mouseenter)="focused = true" (mouseleave)="focused = false">
            <i class="fa icon-{{action.type}}"></i>
            <span>{{action.type}}</span>
            <div [hidden]="!focused" class="panel panel-default">
              <div class="panel-heading">
                <h3 class="panel-title">{{action.type}}</h3>
              </div>
              <div class="panel-body">
                <p *ngFor="#property of action.properties">{{property.id}}={{property.value}}</p>
              </div>
            </div>
        </div>`
})
export class TestActionComponent {

    @Input() action: TestAction;

    @Output() selected = new EventEmitter(true);

    focused = false;

    constructor() {}

    select() {
        this.selected.emit(this.action);
    }

}