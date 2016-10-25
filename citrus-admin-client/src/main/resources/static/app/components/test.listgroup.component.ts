import {Component, Input, Output, EventEmitter, OnInit, AfterViewInit} from '@angular/core';
import {TestGroup, Test} from "../model/tests";

@Component({
    selector: "test-listgroup",
    template: `<div *ngIf="groups.length == 0">
  <div class="group-heading">
    <h3 class="group-title"><i class="fa fa-cubes"></i> No tests found</h3>
  </div>
</div>

<div *ngFor="let group of groups">
  <div (click)="activate(group.name)" class="group-heading clickable">
    <h3 class="group-title"><i class="fa fa-cubes"></i> {{group.name}} <i *ngIf="isActive(group.name)" class="fa fa-caret-down pull-right"></i></h3>
  </div>
  <div>
    <ul *ngIf="isActive(group.name)" class="list-group">
      <li *ngFor="let test of group.tests" class="list-group-item clickable" (click)="open(test)"><span><i class="fa fa-file-text-o"></i> {{test.name}}</span></li>
    </ul>
  </div>
</div>`
})
export class TestListGroupComponent {

    @Input() groups: TestGroup[] = [];

    @Output() selected = new EventEmitter(true);

    activeGroup: string = "";

    activate(groupName : string) {
        if (this.activeGroup.length > 1 && this.activeGroup == groupName) {
            this.activeGroup = "";
        } else {
            this.activeGroup = groupName;
        }
    }

    isActive(groupName: string) {
        return this.activeGroup.length == 1 || this.activeGroup == groupName;
    }

    open(test: Test) {
        this.selected.emit(test);
    }
}