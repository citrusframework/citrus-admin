import {Component, Input, OnInit} from '@angular/core';
import {TestAction, TestDetail} from "../../../../model/tests";
import {TestService} from "../../../../service/test.service";

@Component({
    selector: "test-designer",
    template: `<div class="row test-designer">
  <div id="sidebar" class="col-md-2">
      <div class="start-action" (click)="selectedAction = undefined">
          <i class="fa fa-play"></i>
      </div>

      <div *ngFor="let action of detail.actions; let i = index">
          <div *ngIf="newAction && newActionIndex == i">
              <test-transition [actionIndex]="-1"></test-transition>
              <test-action [action]="newAction" (selected)="onSelected($event)" (removed)="onRemoved($event)"></test-action>
          </div>
          
          <test-transition [actionIndex]="i" (added)="onAdded($event)"></test-transition>
          <test-action *ngIf="action.actions?.length == 0" [action]="action" (selected)="onSelected($event)" (removed)="onRemoved($event)"></test-action>
          <test-container *ngIf="action.actions?.length > 0" [container]="action" (selected)="onSelected($event)" (removed)="onRemoved($event)"></test-container>
      </div>

      <div *ngIf="!newAction && detail.actions.length == 0">
          <test-transition [actionIndex]="-1"></test-transition>
          <div class="add-action" (click)="onAdded(0)">
              <i class="fa fa-plus"></i>
          </div>
      </div>

      <div *ngIf="newAction && detail.actions.length == newActionIndex">
          <test-transition [actionIndex]="-1"></test-transition>
          <test-action [action]="newAction" (selected)="onSelected($event)" (removed)="onRemoved($event)"></test-action>
      </div>

      <test-transition [actionIndex]="detail.actions.length ? detail.actions.length : -1" (added)="onAdded($event)"></test-transition>

      <div class="stop-action" (click)="selectedAction = undefined">
          <i class="fa fa-stop"></i>
      </div>
  </div>  
  <div id="main" class="col-md-10">
     <div *ngIf="!selectedAction">
         <h1>{{detail.name}}</h1>
     </div>

    <div *ngIf="selectedAction && selectedAction === newAction && selectedAction.type != 'new'" class="btn-group pull-right">
      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">ActionType <span class="caret"></span></button>
      <ul class="dropdown-menu">
          <li *ngFor="let type of actionTypes" role="presentation">
              <a role="menuitem" name="all" (click)="selectType(type)" class="clickable"><i class="fa icon-{{type}}"></i> {{type}}</a>
          </li>
      </ul>
    </div>  

    <test-action-form *ngIf="selectedAction"
                    [action]="selectedAction"
                    (saved)="onSave($event)"></test-action-form>
      
    <h1 class="box-center">
        <div *ngIf="selectedAction && selectedAction === newAction && selectedAction.type == 'new'" class="btn-group">
          <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">ActionType <span class="caret"></span></button>
          <ul class="dropdown-menu">
              <li *ngFor="let type of actionTypes" role="presentation">
                  <a role="menuitem" name="all" (click)="selectType(type)" class="clickable"><i class="fa icon-{{type}}"></i> {{type}}</a>
              </li>
          </ul>
        </div>  
    </h1>  
  </div>
</div>`
})
export class TestDesignerComponent {

    @Input() detail: TestDetail;

    newActionIndex: number;
    newAction: TestAction;
    selectedAction: TestAction;

    actionTypes: string[];

    constructor(private testService: TestService) {
        this.testService.getActionTypes()
            .subscribe(res => this.actionTypes = res);
    }

    selectType(type: string) {
        this.testService.getActionType(type)
            .subscribe(res => {
                this.newAction = res;
                this.newAction.dirty = true;
                this.selectedAction = this.newAction;
            });
    }

    onSelected(action: TestAction) {
        this.selectedAction = action;
    }

    onRemoved(action: TestAction) {
        if (action === this.newAction) {
            this.newActionIndex = 0;
            this.newAction = undefined;
        } else {
            this.detail.actions.splice(this.detail.actions.indexOf(action), 1);
        }

        this.selectedAction = undefined;
    }

    onAdded(actionIndex: number) {
        this.newAction = new TestAction();
        this.newAction.type = "new";
        this.newAction.dirty = true;
        this.selectedAction = this.newAction;
        this.newActionIndex = actionIndex;
    }

    onSave(action: TestAction) {
        action.dirty = false;

        if (action === this.newAction) {
            this.detail.actions.splice(this.newActionIndex, 0, this.newAction);
            this.newActionIndex = 0;
            this.newAction = undefined;
            this.selectedAction = undefined;
        } else {
            //ToDo update action
        }
    }
}
