import {Component, Input, Output, EventEmitter, OnInit, OnDestroy} from '@angular/core';
import {Subscription} from "rxjs/Subscription";

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
export class TabsComponent implements OnInit{
    ngOnInit(): void {
        console.log('TabsComponentOnInit')
    }

    ngOnDestroy() {
        console.log('TabsComponentOnDestroy')
    }

    @Input() dynamic: boolean;

    @Output() closed = new EventEmitter<TabComponent>(false);
    @Output() selected = new EventEmitter(true);

    tabs: TabComponent[];

    constructor() {
        this.tabs = [];
    }

    select(tab: TabComponent) {
        if(this.dynamic) {
            this.tabs.forEach((tab: TabComponent) => {
                tab.active = false;
            });
            tab.active = true;
        }
        this.selected.emit(tab);
    }

    close(tab: TabComponent, e:MouseEvent) {
        e.stopPropagation();
        if(this.dynamic) {
            this.tabs.splice(this.tabs.indexOf(tab), 1);
            this.closed.emit(tab);

            if (tab.active && this.tabs.length) {
                this.select(this.tabs[0]);
            }
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
        console.log(this.tabs.length);
    }

    removeTab(tabComponent: TabComponent) {
        this.tabs = this.tabs.filter(t => t !== tabComponent);
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
export class TabComponent implements OnInit, OnDestroy {

    private subscription = new Subscription;

    @Input('tab-id') id: string;
    @Input('tab-title') title: string;
    @Input() closable: boolean;

    @Output() select = new EventEmitter<TabComponent>();
    @Output() close = new EventEmitter<TabComponent>();

    @Input() active: boolean;

    constructor(private tabs: TabsComponent){
        tabs.addTab(this);
    }

    ngOnInit() {
        console.log('Tab_init');
        this.subscription.add(
            this.tabs.closed.filter(tab => tab === this).subscribe(() => this.close.next(this))
        );
        this.subscription.add(
            this.tabs.selected.filter(tab => tab === this).subscribe(() => this.select.next(this))
        );
    }

    ngOnDestroy() {
        this.tabs.removeTab(this)
        this.subscription.unsubscribe();
    }

    onSelect($event:MouseEvent) {
        this.select.emit(this);
    }

}
