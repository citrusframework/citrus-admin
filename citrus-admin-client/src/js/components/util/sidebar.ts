import { Component, Input, Output, EventEmitter } from 'angular2/core';
import { NgFor } from 'angular2/common';

@Component({
    selector: 'sidebar-menu',
    template:`
    <div class="row">
        <div class="col-lg-1">
            <ul class="nav nav-pills nav-stacked" style="display: inline-block;">
              <li *ngFor="#item of items" [class.active]="item.active">
                <a href="{{item.id}}" (click)="select(item, $event)"><i *ngIf="item.icon" class="{{item.icon}}"></i>&nbsp;{{item.title}}</a>
              </li>
            </ul>
        </div>
        <div class="col-lg-11">
          <ng-content></ng-content>
        </div>
    </div>
  `,
    directives: [NgFor]
})
export class SidebarMenu {
    @Output() selected = new EventEmitter(true);

    items: MenuItem[];

    constructor() {
        this.items = [];
    }

    select(item: MenuItem, event) {
        this.items.forEach((item: MenuItem) => {
            item.active = false;
        });
        item.active = true;

        this.selected.emit(item);

        event.stopPropagation();
        return false;
    }

    addMenuItem(item: MenuItem) {
        this.items.push(item);
    }
}

@Component({
    selector: 'item',
    template: `
    <div id="{{id}}" class="tab-pane" [hidden]="!active">
      <ng-content></ng-content>
    </div>
  `
})
export class MenuItem {
    @Input('item-id') id: string;
    @Input('item-title') title: string;
    @Input('item-icon') icon: string;
    @Input() active: boolean;

    constructor(items: SidebarMenu){
        items.addMenuItem(this);
    }
}
