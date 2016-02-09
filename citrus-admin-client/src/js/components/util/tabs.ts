import { Component, Input } from 'angular2/core';
import { NgFor } from 'angular2/common';

@Component({
    selector: 'tabs',
    template:`
    <ul class="nav nav-tabs">
      <li *ngFor="#tab of tabs" [class.active]="tab.active">
        <a href="{{tab.id}}" (click)="select(pill, $event)">{{tab.title}}</a>
      </li>
    </ul>
    <ng-content></ng-content>
  `,
    directives: [NgFor]
})
export class Tabs {

    tabs: Tab[];

    constructor() {
        this.tabs = [];
    }

    select(tab: Tab, event) {
        this.tabs.forEach((tab: Tab) => {
            tab.active = false;
        });
        tab.active = true;

        event.stopPropagation();
        return false;
    }

    addTab(tab: Tab) {
        this.tabs.push(tab);
    }
}

@Component({
    selector: 'tab',
    template: `
    <div id="{{id}}" class="tab-pane" [hidden]="!active">
      <ng-content></ng-content>
    </div>
  `
})
export class Tab {
    @Input('tab-id') id: string;
    @Input('tab-title') title: string;
    @Input() active: boolean;

    constructor(tabs: Tabs){
        tabs.addTab(this);
    }
}
