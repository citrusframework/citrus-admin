import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';

@Component({
    selector: 'tabs',
    template:`
    <ul class="nav nav-tabs">
      <li *ngFor="let tab of tabs" [class.active]="tab.active">
        <button *ngIf="tab.closable" (click)="close(tab, $event)" type="button" class="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <a (click)="select(tab)" name="{{tab.id}}">{{tab.title}}</a>
      </li>
    </ul>
    <ng-content></ng-content>
  `
})
export class TabsComponent {

    @Input() dynamic: boolean;

    @Output() closed = new EventEmitter<TabComponent>(false);
    @Output() selected = new EventEmitter(true);

    tabs: TabComponent[];

    constructor() {
        this.tabs = [];
    }

    select(tab: TabComponent) {
        this.tabs.forEach((tab: TabComponent) => {
            tab.active = false;
        });
        tab.active = true;
        this.selected.emit(tab);
    }

    close(tab: TabComponent, e:MouseEvent) {
        e.stopPropagation();
        this.tabs.splice(this.tabs.indexOf(tab), 1);
        this.closed.emit(tab);

        if (tab.active && this.tabs.length) {
            this.select(this.tabs[0]);
        }
    }

    addTab(tab: TabComponent) {
        if (this.dynamic) {
            this.tabs.forEach((tab: TabComponent) => {
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
    <div id="{{id}}" class="tab-pane" [hidden]="!active" (click)="onSelect($event)">
      <ng-content></ng-content>
    </div>
  `
})
export class TabComponent implements OnInit {
    @Input('tab-id') id: string;
    @Input('tab-title') title: string;
    @Input() closable: boolean;

    @Output() select = new EventEmitter<TabComponent>();
    @Output() close = new EventEmitter<TabComponent>();

    active: boolean;

    constructor(private tabs: TabsComponent){
        tabs.addTab(this);
    }

    ngOnInit() {
        this.tabs.closed.filter(tab => tab === this).subscribe(() => this.close.next(this))
        this.tabs.selected.filter(tab => tab === this).subscribe(() => this.select.next(this))
    }

}
