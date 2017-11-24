import {Component, Input, OnInit, Output, EventEmitter, OnChanges, SimpleChanges} from "@angular/core";
import {Observable} from "rxjs";
import {Property} from "../../../../model/property";
import {SpringBeanService} from "../../../../service/springbean.service";
import {FormBuilder, FormGroup, Validators, ValidatorFn, AsyncValidatorFn} from "@angular/forms";
import {IdMap} from "../../../../util/redux.util";
import {RestrictAsyncValues} from "../../../util/autocomplete";
import {TestAction} from "../../../../model/tests";

@Component({
    selector: 'test-action-form',
    templateUrl: 'test-action-form.html'
})
export class TestActionFormComponent implements OnChanges {
    @Input() action:TestAction;

    @Output() save = new EventEmitter<TestAction>();

    form:FormGroup;

    beans:{[propKey:string]:Observable<string[]>} = {};

    constructor(
        private fb:FormBuilder,
        private springBeanService:SpringBeanService
    ) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.beans = this.action.properties
            .filter(p => p.optionType)
            .reduce((beans, p) => ({...beans, [p.optionType]:this.springBeanService.searchBeans(p.optionType)}), {});

        this.form = this.fb.group({
            ...this.action.properties
                .reduce((fg,p) => ({...fg, [p.name]: [p.value, ...this.getValidators(p)]}), {} as IdMap<any>)
        })
    }

    private getValidators(p: Property):[ValidatorFn, AsyncValidatorFn] {
        const validators:ValidatorFn[] = [(c) => null];
        const asyncValidators:AsyncValidatorFn[] = [];
        if (p.required) {
            validators.push(Validators.required)
        }

        if (p.optionType) {
            asyncValidators.push(RestrictAsyncValues(this.beans[p.optionType]))
        }
        return [
            Validators.compose(validators),
            Validators.composeAsync(asyncValidators)
        ];
    }

    invokeSave(action:TestAction) {
        this.save.next(action);
    }
}
