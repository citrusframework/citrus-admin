import {Component,  Input, Output, EventEmitter} from '@angular/core';
import {TestAction} from "../../../../model/tests";

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
                <span [textContent]="getProperty()"></span>
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

    getProperty() {
        let propertyName: string;

        switch (this.action.type) {
            case "send":
                propertyName = "endpoint";
                break;
            case "receive":
                propertyName = "endpoint";
                break;
            case "sleep":
                propertyName = "milliseconds";
                break;
            case "echo":
                propertyName = "message";
                break;
            default:
                propertyName = "all";
        }

        let property = this.action.properties.find(p => p.id === propertyName);

        if (property) {
            return property.id + " => " + property.value;
        } else {
            return this.action.properties.map(p => p.id + "=" + p.value).concat();
        }
    }

}