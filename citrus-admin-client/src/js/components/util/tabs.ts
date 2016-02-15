import { Component, Input, Output, EventEmitter } from 'angular2/core';
import { NgFor } from 'angular2/common';

@Component({
    selector: 'tabs',
    template:`
    <ul class="nav nav-tabs">
      <li *ngFor="#tab of tabs" [class.active]="tab.active">
        <button *ngIf="tab.closable" (click)="close(tab)" type="button" class="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <a (click)="select(tab)" name="{{tab.id}}">{{tab.title}}</a>
      </li>
    </ul>
    <ng-content></ng-content>
  `,
    directives: [NgFor]
})
export class Tabs {

    @Input() dynamic: boolean;

    @Output() closed = new EventEmitter(false);
    @Output() selected = new EventEmitter(true);

    tabs: Tab[];

    constructor() {
        this.tabs = [];
    }

    select(tab: Tab) {
        this.tabs.forEach((tab: Tab) => {
            tab.active = false;
        });
        tab.active = true;

        this.selected.emit(tab);
    }

    close(tab: Tab) {
        this.tabs.splice(this.tabs.indexOf(tab), 1);
        this.closed.emit(tab);

        if (tab.active && this.tabs.length) {
            this.select(this.tabs[0]);
        }
    }

    addTab(tab: Tab) {
        if (this.dynamic) {
            this.tabs.forEach((tab: Tab) => {
                tab.active = false;
            });
            tab.active = true;
        } else if (this.tabs.length === 0) {
            tab.active = true;
        }

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
    @Input() closable: boolean;

    active: boolean;

    constructor(tabs: Tabs){
        tabs.addTab(this);
    }
}
