import {Component, Input, OnInit} from '@angular/core';
import {TestAction, TestDetail} from "../../../../model/tests";
import {TestService} from "../../../../service/test.service";
import {Variable} from "../../../../model/variable";

@Component({
    selector: "test-designer",
    templateUrl: "test-designer.html"
})
export class TestDesignerComponent {

    @Input() detail: TestDetail;

    newActionIndex: number;
    newAction: TestAction;
    variable: Variable = new Variable();
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
            console.log(action);
        }
    }

    addVariable() {
        if (this.variable.name) {
            this.detail.variables.push(this.variable);
            this.variable = new Variable();
        }
    }

    removeVariable(variable: Variable, event:MouseEvent) {
        this.detail.variables.splice(this.detail.variables.indexOf(variable), 1);
        event.stopPropagation();
        return false;
    }
}
