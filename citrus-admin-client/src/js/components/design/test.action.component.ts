import {Component,  Input, Output, EventEmitter} from 'angular2/core';
import {NgSwitch} from "angular2/common";
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
                <div [ngSwitch]="action.type">
                  <template [ngSwitchWhen]="'send'">
                    <span>{{getProperty("endpoint")}}</span>
                  </template>
                  <template [ngSwitchWhen]="'receive'">
                    <span>{{getProperty("endpoint")}}</span>
                  </template>
                  <template [ngSwitchWhen]="'sleep'">
                    <span>{{getProperty("time")}}</span>
                  </template>
                  <template [ngSwitchWhen]="'echo'">
                    <span>{{getProperty("message")}}</span>
                  </template>
                  <template ngSwitchDefault>
                    <p *ngFor="#property of action.properties">{{property.id}}={{property.value}}</p>
                  </template>
                </div>
              </div>
            </div>
        </div>`,
    directives: [NgSwitch]
})
export class TestActionComponent {

    @Input() action: TestAction;

    @Output() selected = new EventEmitter(true);

    focused = false;

    constructor() {}

    select() {
        this.selected.emit(this.action);
    }

    getProperty(name: string) {
        var property = this.action.properties.find(p => p.id === name);
        return property.value;
    }

}