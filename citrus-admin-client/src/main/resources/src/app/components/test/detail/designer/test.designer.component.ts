import {Component, Input, OnInit} from '@angular/core';
import {TestAction, TestDetail} from "../../../../model/tests";
import {TestService} from "../../../../service/test.service";
import {Variable} from "../../../../model/variable";
import {AlertService} from "../../../../service/alert.service";
import {Alert} from "../../../../model/alert";

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

    constructor(private testService: TestService,
                private _alertService: AlertService) {
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
            this.testService.deleteAction(this.detail.actions.indexOf(action), this.detail)
                .subscribe(res => {
                        this.detail.actions.splice(this.detail.actions.indexOf(action), 1);
                    },
                    error => this.notifyError(error));
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
        if (action === this.newAction) {
            this.detail.actions.splice(this.newActionIndex, 0, this.newAction);

            this.testService.addAction(this.newActionIndex, this.detail)
                .subscribe(res => {
                        this.newActionIndex = 0;
                        this.newAction = undefined;
                        this.selectedAction = undefined;
                        action.dirty = false;
                },
                error => this.notifyError(error));
        } else {
            this.testService.updateAction(this.detail.actions.indexOf(action), this.detail)
                .subscribe(res => {
                        this.selectedAction = undefined;
                        action.dirty = false;
                    },
                    error => this.notifyError(error));
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

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", JSON.stringify(error), false));
    }
}
