import { Component, Input } from 'angular2/core';
import { NgFor } from 'angular2/common';

@Component({
    selector: 'pills',
    template:`
    <ul class="nav nav-pills">
      <li *ngFor="#pill of pills" [class.active]="pill.active">
        <a href="{{pill.id}}" (click)="select(pill, $event)">{{pill.title}}</a>
      </li>
    </ul>
    <ng-content></ng-content>
  `,
    directives: [NgFor]
})
export class Pills {

    pills: Pill[];

    constructor() {
        this.pills = [];
    }

    select(pill: Pill, event) {
        this.pills.forEach((pill: Pill) => {
            pill.active = false;
        });
        pill.active = true;

        event.stopPropagation();
        return false;
    }

    addPill(pill: Pill) {
        this.pills.push(pill);
    }
}

@Component({
    selector: 'pill',
    template: `
    <div id="{{id}}" class="tab-pane" [hidden]="!active">
      <ng-content></ng-content>
    </div>
  `
})
export class Pill {
    @Input('pill-id') id: string;
    @Input('pill-title') title: string;
    @Input() active: boolean;

    constructor(pills: Pills){
        pills.addPill(this);
    }
}
